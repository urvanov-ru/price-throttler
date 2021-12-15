package ru.urvanov.luxoft.pricethrottler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PriceThrottler implements PriceProcessor, Consumer<OnPriceTask> {
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Last rates.
     */
    private Map<String, Double> lastRate = new ConcurrentHashMap<>();

    /**
     * Data for every PriceProcessor.
     */
    private Map<PriceProcessor, PriceProcessorData> priceProcessors = new ConcurrentHashMap<>();


    @Override
    public void onPrice(String ccyPair, double rate) {
        lastRate.put(ccyPair, rate);
        for (Map.Entry<PriceProcessor, PriceProcessorData> entry : priceProcessors.entrySet()) {
            PriceProcessor priceProcessor = entry.getKey();
            PriceProcessorData priceProcessorData = entry.getValue();
            synchronized (priceProcessorData) {
                Set<String> ccy = priceProcessorData.getCcyPairs();
                if ((priceProcessorData.getTask() == null) || (priceProcessorData.getTask().isDone())) {
                    OnPriceTask onPriceTask = new OnPriceTask(ccyPair, rate, priceProcessor, this);
                    priceProcessorData.setTask(executorService.submit(onPriceTask));
                } else {
                    // add ccyPair to process after priceProcessor finishes current task
                    ccy.add(ccyPair);
                }
            }
        }
    }

    @Override
    public void unsubscribe(PriceProcessor priceProcessor) {
        priceProcessors.remove(priceProcessor);
    }

    @Override
    public void subscribe(PriceProcessor priceProcessor) {
        priceProcessors.put(priceProcessor, new PriceProcessorData());
    }

    /**
     * Callbacks from OnPriceTask
     * @param onPriceTask
     */
    @Override
    public void accept(OnPriceTask onPriceTask) {
        PriceProcessorData priceProcessorData = priceProcessors.get(onPriceTask.getPriceProcessor());
        if (priceProcessorData == null) {
            // Looks like priceProcessor called unsubscribe.
            return;
        }
        synchronized (priceProcessorData) {
            Set<String> ccyPairs = priceProcessorData.getCcyPairs();
            ccyPairs.remove(onPriceTask.getCcyPair());
            ccyPairs.stream().findFirst().ifPresent(c -> {
                priceProcessorData.setTask(
                        executorService.submit(
                                new OnPriceTask(c, lastRate.get(c), onPriceTask.getPriceProcessor(), this)));
            });

        }
    }
}
