package com.binance.api.client.impl;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.exception.BinanceApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Binance API WebSocket listener.
 */
public class BinanceApiWebSocketListener<T> extends WebSocketListener {

  private BinanceApiCallback<T> callback;
  private Consumer<Throwable> errorHandler;

  private Class<T> eventClass;

  public BinanceApiWebSocketListener(BinanceApiCallback<T> callback, Class<T> eventClass) {
    this.callback = callback;
    this.eventClass = eventClass;
  }

  public BinanceApiWebSocketListener(BinanceApiCallback<T> callback, Class<T> eventClass, Consumer<Throwable> errorHandler) {
    this.callback = callback;
    this.eventClass = eventClass;
    this.errorHandler = errorHandler;
  }

  @Override
  public void onMessage(WebSocket webSocket, String text) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      T event = mapper.readValue(text, eventClass);
      callback.onResponse(event);
    } catch (IOException e) {
      if (errorHandler != null) errorHandler.accept(e);
      else throw new BinanceApiException(e);
    }
  }

  @Override
  public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    if (errorHandler != null) errorHandler.accept(t);
    else throw new BinanceApiException(t);
  }
}