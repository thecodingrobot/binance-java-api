package com.binance.api.client;

public interface WebSocketConnection {
    boolean close(int code, String reason);
}
