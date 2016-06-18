package com.unseenspace.dynalogic;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Created by madsk_000 on 6/17/2016.
 */
public class DelayedOperator<T> implements AutoCloseable {
    private ConcurrentMap<T, TimerTask> map = new ConcurrentHashMap<>();
    private Timer timer = new Timer("DelayedOperator.timer", true);
    private Consumer<T> consumer;
    private long delay;

    public DelayedOperator(Consumer<T> consumer, long delay) {
        this.consumer = consumer;
        this.delay = delay;
    }

    public void put(T value) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                consumer.accept(value);
            }
        };

        TimerTask oldTask = map.put(value, task);
        if (oldTask != null)
            oldTask.cancel();
        timer.schedule(task, delay);
    }


    public void remove(T value) {
        TimerTask oldTask = map.remove(value);
        if (oldTask != null)
            oldTask.cancel();
    }

    @Override
    public void close() {
        timer.purge();
        timer.cancel();
        timer = null;
    }
}
