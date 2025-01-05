package com.company.customeremulation.service;

import com.company.customeremulation.entity.OrderDto;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class BackgroundOrdersStream extends BackgroundTask<Integer, Void> {

    private final OrderGenerator orderGenerator;

    public BackgroundOrdersStream(long timeout,
                                  OrderGenerator orderGenerator) {
        super(timeout, TimeUnit.MINUTES);
        this.orderGenerator = orderGenerator;
    }

    @Override
    public Void run(TaskLifeCycle taskLifeCycle) throws Exception {
        int i = 0;
        while (!taskLifeCycle.isCancelled()) {
            OrderDto order = orderGenerator.generate();
            //noinspection unchecked
            taskLifeCycle.publish(order);
            try {
                //noinspection BusyWait
                sleep(orderGenerator.randomDelay());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
   }

}


