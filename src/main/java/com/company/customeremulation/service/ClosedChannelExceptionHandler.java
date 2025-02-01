package com.company.customeremulation.service;

import io.jmix.flowui.Notifications;
import io.jmix.flowui.exception.AbstractUiExceptionHandler;
import org.springframework.stereotype.Component;

import java.nio.channels.ClosedChannelException;

@Component
public class ClosedChannelExceptionHandler extends AbstractUiExceptionHandler {
    private final Notifications notifications;

    public ClosedChannelExceptionHandler(Notifications notifications) {
        super(ClosedChannelException.class.getName());
        this.notifications = notifications;
    }

    @Override
    protected void doHandle(String className, String message, Throwable throwable) {
        notifications.create("The data source is currently unavailable. Please try again later.")
                .withType(Notifications.Type.ERROR)
                .show();
    }
}
