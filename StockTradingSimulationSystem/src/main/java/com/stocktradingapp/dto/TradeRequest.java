package com.stocktradingapp.dto;



import lombok.Data;

@Data
public class TradeRequest {
    private Long userId;
    private String stockSymbol;
    private int quantity;
    private double price;
    private String tradeType;
}

