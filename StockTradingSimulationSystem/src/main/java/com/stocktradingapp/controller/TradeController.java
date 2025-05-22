package com.stocktradingapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stocktradingapp.dto.TradeRequest;
import com.stocktradingapp.entity.Trade;
import com.stocktradingapp.service.TradeService;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<?> buy(@RequestBody TradeRequest request) {
        System.out.println("Received BUY request: " + request); // ✅ Log request
        try {
            Trade trade = tradeService.makeTrade(
                request.getUserId(),
                request.getStockSymbol(),
                request.getQuantity(),
                request.getPrice(),
                "BUY"
            );
            return ResponseEntity.ok(trade);
        } catch (RuntimeException e) {
            System.err.println("Error processing BUY: " + e.getMessage()); // ✅ Log error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sell(@RequestBody TradeRequest request) {
        System.out.println("Received SELL request: " + request); // ✅ Log request
        try {
            Trade trade = tradeService.makeTrade(
                request.getUserId(),
                request.getStockSymbol(),
                request.getQuantity(),
                request.getPrice(),
                "SELL"
            );
            return ResponseEntity.ok(trade);
        } catch (RuntimeException e) {
            System.err.println("Error processing SELL: " + e.getMessage()); // ✅ Log error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> placeTrade(@RequestBody TradeRequest tradeRequest) {
        System.out.println("Received trade request: " + tradeRequest); // ✅ Log

        try {
            String tradeType = tradeRequest.getTradeType(); // "BUY" or "SELL"

            Trade trade = tradeService.makeTrade(
                tradeRequest.getUserId(),
                tradeRequest.getStockSymbol(),
                tradeRequest.getQuantity(),
                tradeRequest.getPrice(),
                tradeType
            );

            return ResponseEntity.ok(trade);
        } catch (RuntimeException e) {
            System.err.println("Error processing trade: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<List<Trade>> getUserTrades(@PathVariable Long id) {
        System.out.println("Fetching trades for userId: " + id); // ✅ Log
        return ResponseEntity.ok(tradeService.getUserTrades(id));
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<?> getPortfolio(@PathVariable Long userId) {
        System.out.println("Calculating portfolio for userId: " + userId); // ✅ Log
        try {
            Map<String, Object> portfolio = tradeService.calculatePortfolio(userId);
            return ResponseEntity.ok(portfolio);
        } catch (RuntimeException e) {
            System.err.println("Error calculating portfolio: " + e.getMessage()); // ✅ Log error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
