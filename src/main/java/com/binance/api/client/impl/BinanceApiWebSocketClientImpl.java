package com.binance.api.client.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiFullCallback;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.WebSocketConnection;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.event.Tuple;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.AllMarketTickersEvent;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.market.CandlestickInterval;

/**
 * Binance API WebSocket client implementation using OkHttp.
 */
public class BinanceApiWebSocketClientImpl implements BinanceApiWebSocketClient, Closeable {

  private OkHttpClient client;

  public BinanceApiWebSocketClientImpl() {
    Dispatcher d = new Dispatcher();
    d.setMaxRequestsPerHost(100);
    this.client = new OkHttpClient.Builder().dispatcher(d).build();
  }

  public void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback) {
    final String channel = String.format("%s@depth", symbol);
    createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, DepthEvent.class));
  }

  @Override
  public void onCandlestickEvent(String symbol, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> callback) {
    final String channel = String.format("%s@kline_%s", symbol, interval.getIntervalId());
    createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, CandlestickEvent.class));
  }

  public void onAggTradeEvent(String symbol, BinanceApiCallback<AggTradeEvent> callback) {
    final String channel = String.format("%s@aggTrade", symbol);
    createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, AggTradeEvent.class));
  }

  @Override
  public void onAggTradeEvent(String symbol, BinanceApiFullCallback<AggTradeEvent> callback) {
    final String channel = String.format("%s@aggTrade", symbol);
    createNewWebSocket(channel, new BinanceApiWebSocketListener<>(callback, AggTradeEvent.class, callback::onFailure));
  }

  @Override
  public WebSocketConnection onAggTradeEvent(String[] symbols, BinanceApiFullCallback<Tuple<AggTradeEvent>> callback) {
    final String[] channels = new String[symbols.length];
    for (int i = 0; i < channels.length; i++) {
      channels[i] = String.format("%s@aggTrade", symbols[i]);
    }
    return createNewWebSocket(channels, new BinanceApiWebSocketStreamsListener<>(callback, callback::onFailure, AggTradeEvent.class));
  }

  public void onUserDataUpdateEvent(String listenKey, BinanceApiCallback<UserDataUpdateEvent> callback) {
    createNewWebSocket(listenKey, new BinanceApiWebSocketListener<>(callback, UserDataUpdateEvent.class));
  }

  public WebSocketConnection onUserDataUpdateEvent(String[] listenKeys, BinanceApiCallback<Tuple<UserDataUpdateEvent>> callback) {
    return createNewWebSocket(listenKeys, new BinanceApiWebSocketStreamsListener<>(callback, null, UserDataUpdateEvent.class));
  }

  @Override
  public void onUserDataUpdateEvent(String listenKey, BinanceApiFullCallback<UserDataUpdateEvent> callback) {
    createNewWebSocket(listenKey, new BinanceApiWebSocketListener<>(callback, UserDataUpdateEvent.class));
  }

  public void onAllMarketTickersEvent(BinanceApiCallback<List<AllMarketTickersEvent>> callback) {
    final String channel = "!ticker@arr";
    createNewWebSocket(channel, new BinanceApiWebSocketListener<List<AllMarketTickersEvent>>(callback));
  }

  @Override
  public WebSocketConnection onUserDataUpdateEvent(String[] listenKeys, BinanceApiFullCallback<Tuple<UserDataUpdateEvent>> callback) {
    return createNewWebSocket(listenKeys, new BinanceApiWebSocketStreamsListener<>(callback, callback::onFailure, UserDataUpdateEvent.class));
  }

  private void createNewWebSocket(String channel, WebSocketListener listener) {
    String streamingUrl = String.format("%s/ws/%s", BinanceApiConstants.WS_API_BASE_URL, channel);
    Request request = new Request.Builder().url(streamingUrl).build();
    client.newWebSocket(request, listener);
  }

  private WebSocketConnection createNewWebSocket(String[] channels, WebSocketListener listener) {
    String streamingUrl = String.format("%s/stream?streams=%s", BinanceApiConstants.WS_API_BASE_URL, String.join("/", channels));
    Request request = new Request.Builder().url(streamingUrl).build();
    WebSocket webSocket = client.newWebSocket(request, listener);
    return webSocket::close;
  }

  @Override
  public void close() throws IOException {
    client.dispatcher().executorService().shutdown();
  }

}
