package com.binance.api.client.impl;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiFullCallback;
import com.binance.api.client.domain.event.Tuple;
import com.binance.api.client.exception.BinanceApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Binance API WebSocket listener.
 */
public class BinanceApiWebSocketStreamsListener<T> extends WebSocketListener {

    private final Class<T> entityClass;
    private final BinanceApiCallback<Tuple<T>> callback;
    private final Consumer<Throwable> errorCallback;

    public BinanceApiWebSocketStreamsListener(BinanceApiCallback<Tuple<T>> callback, Consumer<Throwable> errorCallback, Class<T> entityClass) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.entityClass = entityClass;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = mapper.readTree(text);
            String stream = json.get("stream").asText();
            T data = mapper.treeToValue(json.get("data"), entityClass);

            callback.onResponse(new Tuple<>(stream, data));
        } catch (IOException e) {
            if (errorCallback != null)
                errorCallback.accept(e);
            else throw new BinanceApiException(e);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (errorCallback != null)
            errorCallback.accept(t);
        else throw new BinanceApiException(t);
    }
}