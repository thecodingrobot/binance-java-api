package com.binance.api.client;

import java.util.List;
import com.binance.api.client.domain.event.Tuple;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.AllMarketTickersEvent;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.market.CandlestickInterval;

/**
 * Binance API data streaming façade, supporting streaming of events through web sockets.
 */
public interface BinanceApiWebSocketClient {

  void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback);

  void onCandlestickEvent(String symbol, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> callback);

  void onAggTradeEvent(String symbol, BinanceApiCallback<AggTradeEvent> callback);

  void onAggTradeEvent(String symbol, BinanceApiFullCallback<AggTradeEvent> callback);

  WebSocketConnection onAggTradeEvent(String[] symbols, BinanceApiFullCallback<Tuple<AggTradeEvent>> callback);

  void onUserDataUpdateEvent(String listenKey, BinanceApiCallback<UserDataUpdateEvent> callback);

  WebSocketConnection onUserDataUpdateEvent(String[] listenKeys, BinanceApiCallback<Tuple<UserDataUpdateEvent>> callback);

  void onUserDataUpdateEvent(String listenKey, BinanceApiFullCallback<UserDataUpdateEvent> callback);

  WebSocketConnection onUserDataUpdateEvent(String[] listenKeys, BinanceApiFullCallback<Tuple<UserDataUpdateEvent>> callback);
  
  void onAllMarketTickersEvent(BinanceApiCallback<List<AllMarketTickersEvent>> callback);
}
