package com.binance.api.client.impl;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiFullCallback;
import com.binance.api.client.exception.BinanceApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.IOException;

/**
 * Binance API WebSocket listener.
 */
public class BinanceApiWebSocketFullListener<T> extends WebSocketListener {

  private BinanceApiFullCallback<T> callback;

  private Class<T> eventClass;

  public BinanceApiWebSocketFullListener(BinanceApiFullCallback<T> callback, Class<T> eventClass) {
    this.callback = callback;
    this.eventClass = eventClass;
  }

  @Override
  public void onMessage(WebSocket webSocket, String text) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      T event = mapper.readValue(text, eventClass);
      callback.onResponse(event);
    } catch (IOException e) {
      callback.onFailure(e);
    }
  }

  @Override
  public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    callback.onFailure(t);
  }

  @Override
  public void onClosing(WebSocket webSocket, int code, String reason) {
    webSocket.close(code, reason);
  }
}