package com.stocktradingapp.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stocktradingapp.entity.Trade;
import com.stocktradingapp.entity.User;
import com.stocktradingapp.repository.TradeRepository;
import com.stocktradingapp.repository.UserRepository;

@Service
public class TradeService {
    @Autowired
    private TradeRepository tradeRepo;

    @Autowired
    private UserRepository userRepo;

    public Trade makeTrade(Long userId, String symbol, int qty, double price, String type) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if ("BUY".equalsIgnoreCase(type)) {
            double totalCost = qty * price;
            if (user.getBalance() < totalCost) {
                throw new RuntimeException("Insufficient balance");
            }
            user.setBalance(user.getBalance() - totalCost);

        } else if ("SELL".equalsIgnoreCase(type)) {
            // Check if user owns enough shares to sell
            int ownedQty = getUserStockQuantity(userId, symbol);
            if (ownedQty < qty) {
                throw new RuntimeException("Not enough shares to sell");
            }
            user.setBalance(user.getBalance() + qty * price);

        } else {
            throw new RuntimeException("Invalid trade type. Use BUY or SELL.");
        }

        userRepo.save(user);

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStockSymbol(symbol);
        trade.setQuantity(qty);
        trade.setPrice(price);
        trade.setType(type.toUpperCase());
        trade.setTradeTime(LocalDateTime.now());

        return tradeRepo.save(trade);
    }

    public List<Trade> getUserTrades(Long userId) {
        return tradeRepo.findByUserId(userId);
    }

    // Helper to get net owned shares of a stock for a user (buys - sells)
    public int getUserStockQuantity(Long userId, String stockSymbol) {
        List<Trade> trades = tradeRepo.findByUserId(userId);
        int qty = 0;
        for (Trade t : trades) {
            if (t.getStockSymbol().equalsIgnoreCase(stockSymbol)) {
                if ("BUY".equalsIgnoreCase(t.getType())) {
                    qty += t.getQuantity();
                } else if ("SELL".equalsIgnoreCase(t.getType())) {
                    qty -= t.getQuantity();
                }
            }
        }
        return qty;
    }

    // Calculate portfolio summary: stock symbol -> {quantity, avgPrice, currentValue}
    public Map<String, Object> calculatePortfolio(Long userId) {
        List<Trade> trades = tradeRepo.findByUserId(userId);
        if (trades.isEmpty()) {
            throw new RuntimeException("No trades found for user");
        }

        // Aggregate holdings: symbol -> total qty and total cost (to calculate average price)
        Map<String, Integer> quantityMap = new HashMap<>();
        Map<String, Double> costMap = new HashMap<>();

        for (Trade t : trades) {
            String symbol = t.getStockSymbol().toUpperCase();
            int qty = t.getQuantity();
            double price = t.getPrice();

            if ("BUY".equalsIgnoreCase(t.getType())) {
                quantityMap.put(symbol, quantityMap.getOrDefault(symbol, 0) + qty);
                costMap.put(symbol, costMap.getOrDefault(symbol, 0.0) + price * qty);
            } else if ("SELL".equalsIgnoreCase(t.getType())) {
                quantityMap.put(symbol, quantityMap.getOrDefault(symbol, 0) - qty);
                costMap.put(symbol, costMap.getOrDefault(symbol, 0.0) - price * qty);
            }
        }

        // Remove stocks with zero or negative holdings (sold all)
        quantityMap.entrySet().removeIf(entry -> entry.getValue() <= 0);

        // For each stock calculate average price and (dummy) current value (could be from API)
        // For demo, current value = quantity * last trade price (assumed last trade price = price in costMap/qty)
        Map<String, Object> portfolio = new HashMap<>();
        for (String symbol : quantityMap.keySet()) {
            int qty = quantityMap.get(symbol);
            double totalCost = costMap.get(symbol);
            double avgPrice = totalCost / qty;

            // For real app, call external API here to get current price
            double currentPrice = avgPrice; // dummy: assume price unchanged
            double currentValue = qty * currentPrice;

            Map<String, Object> stockInfo = new HashMap<>();
            stockInfo.put("quantity", qty);
            stockInfo.put("averagePrice", avgPrice);
            stockInfo.put("currentPrice", currentPrice);
            stockInfo.put("currentValue", currentValue);

            portfolio.put(symbol, stockInfo);
        }

        // Optionally add total portfolio value
        double totalValue = portfolio.values().stream()
                .mapToDouble(s -> (double)((Map<?, ?>)s).get("currentValue"))
                .sum();

        portfolio.put("totalValue", totalValue);

        return portfolio;
    }
}
