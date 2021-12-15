package ru.urvanov.luxoft.pricethrottler;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Future;

public class PriceProcessorData {

    /**
     * List of ccyPairs to update.
     */
    private final Set<String> ccyPairs = new LinkedHashSet<>();

    /**
     * Running task
     */
    private Future<?> task;

    public Set<String> getCcyPairs() {
        return ccyPairs;
    }

    public Future<?> getTask() {
        return task;
    }

    public void setTask(Future<?> task) {
        this.task = task;
    }

}
