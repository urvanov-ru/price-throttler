package ru.urvanov.luxoft.pricethrottler;

import java.util.Random;

public class MainClass {
    public static void main(String[] args) {
        PriceThrottler priceThrottler = new PriceThrottler();
        class LowProcessor implements PriceProcessor {

            @Override
            public void onPrice(String ccyPair, double rate) {
                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Low " + ccyPair + " " + rate);
            }

            @Override
            public void unsubscribe(ru.urvanov.luxoft.pricethrottler.PriceProcessor priceProcessor) {

            }

            @Override
            public void subscribe(ru.urvanov.luxoft.pricethrottler.PriceProcessor priceProcessor) {

            }
        }

        class HighProcessor implements PriceProcessor {

            @Override
            public void onPrice(String ccyPair, double rate) {
                System.out.println("High " + ccyPair + " " + rate);
            }

            @Override
            public void unsubscribe(ru.urvanov.luxoft.pricethrottler.PriceProcessor priceProcessor) {

            }

            @Override
            public void subscribe(ru.urvanov.luxoft.pricethrottler.PriceProcessor priceProcessor) {

            }
        }

        priceThrottler.subscribe(new LowProcessor());
        priceThrottler.subscribe(new LowProcessor());
        priceThrottler.subscribe(new HighProcessor());
        priceThrottler.subscribe(new HighProcessor());
        priceThrottler.subscribe(new HighProcessor());
        priceThrottler.subscribe(new HighProcessor());


        Random random = new Random();
        for (int n = 0; n < 60; n++) {
            String[] ccyPairs = {"EURUSD", "EURRUB", "USDJPY"};
            String ccyPair = ccyPairs[random.nextInt(3)];
            priceThrottler.onPrice(ccyPair, random.nextDouble(-100.0, 100.0));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
