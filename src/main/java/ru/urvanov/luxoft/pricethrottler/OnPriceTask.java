package ru.urvanov.luxoft.pricethrottler;

import java.util.function.Consumer;

public class OnPriceTask implements Runnable {
    private String ccyPair;
    private double rate;
    private PriceProcessor priceProcessor;
    private Consumer<OnPriceTask> callback;

    public OnPriceTask(String ccyPair, double rate, PriceProcessor priceProcessor, Consumer<OnPriceTask> callback) {
        this.ccyPair = ccyPair;
        this.rate = rate;
        this.priceProcessor = priceProcessor;
        this.callback = callback;
    }

    @Override
    public void run() {
        priceProcessor.onPrice(ccyPair, rate);
        if (callback != null) {
            callback.accept(this);
        }
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public double getRate() {
        return rate;
    }

    public PriceProcessor getPriceProcessor() {
        return priceProcessor;
    }
}
