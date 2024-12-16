package com.company.customeremulation.service;

import com.company.customeremulation.view.demo.DemoView;
import io.jmix.flowui.backgroundtask.BackgroundTask;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;

import java.util.concurrent.TimeUnit;

public class BackgroundOrdersStream extends BackgroundTask<Integer, Void> {
    private volatile boolean isRunning = false;

    private final OrderGenerator orderGenerator;
    private final DemoView demoView;

    public BackgroundOrdersStream(long timeout,
                                  OrderGenerator orderGenerator,
                                  DemoView demoView) {
        super(timeout, TimeUnit.MINUTES);
        this.orderGenerator = orderGenerator;
        this.demoView = demoView;
    }

    @Override
    public Void run(TaskLifeCycle taskLifeCycle) throws Exception {
        int i = 0;
        while (!taskLifeCycle.isCancelled()) {
            int delay = orderGenerator.randomDelay();
            String orderJson = orderGenerator.sendOrder("orders");
            System.out.println("Order: " + i++ + ", Delay: " + delay + ", JSON: " + orderJson);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
   }
    public boolean isTaskRunning() {
        return isRunning;
    }
}


