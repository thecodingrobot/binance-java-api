package com.binance.api.client.domain.event;

public class Tuple<T> {
    private String stream;
    private T data;

    public Tuple() {
    }

    public Tuple(String stream, T data) {
        this.stream = stream;
        this.data = data;
    }

    public String getStream() {
        return stream;
    }

    public T getData() {
        return data;
    }
}