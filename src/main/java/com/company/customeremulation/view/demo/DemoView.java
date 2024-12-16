package com.company.customeremulation.view.demo;


import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.service.*;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static java.time.LocalTime.now;

@Route(value = "demo-view", layout = MainView.class)
@ViewController(id = "cst_DemoView")
@ViewDescriptor(path = "demo-view.xml")
public class DemoView extends StandardView {
    private static final Logger log = LoggerFactory.getLogger(DemoView.class);
    @Autowired
    private Notifications notifications;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;

    private static BackgroundTaskHandler<Void> taskHandler;
    private static BackgroundOrdersStream ordersStream;

    @ViewComponent
    private TypedTextField<Integer> numberField;
    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private OrderGenerator orderGenerator;
    @ViewComponent
    private JmixButton startBtn;
    @ViewComponent
    private JmixButton stopBtn;
    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (ordersStream != null && ordersStream.isTaskRunning()) {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        } else {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }
    }

    @Subscribe(id = "genBtn", subject = "clickListener")
    public void onGenBtnClick(final ClickEvent<JmixButton> event) {
        Integer value = numberField.getTypedValue();
        if (value != null)
            for (int i = 0; i < value; i++) {
                String customerName = customerService.generateCustomer().name();
                notifications.create(customerName).show();
        }
    }

    @Subscribe(id = "orderBtn", subject = "clickListener")
    public void onOrderBtnClick(final ClickEvent<JmixButton> event) {
        OrderDto order = orderGenerator.generate();
        if (order != null)
            notifications.create("Order: " + order.getInstanceName())
                    .withDuration(5000)
                    .show();
        else
            notifications.create("No parameters")
                    .withDuration(3000)
                    .withType(Notifications.Type.ERROR)
                    .show();
    }

    @Subscribe(id = "startBtn", subject = "clickListener")
    public void onStartBtnClick(final ClickEvent<JmixButton> event) {
        ordersStream = new BackgroundOrdersStream(60, orderGenerator, this);
        taskHandler = backgroundWorker.handle(ordersStream);
        taskHandler.execute();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    @Subscribe(id = "stopBtn", subject = "clickListener")
    public void onStopBtnClick(final ClickEvent<JmixButton> event) {
        if (taskHandler != null) {
            taskHandler.cancel();
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }
    }

    @Subscribe(id = "sendOrderBtn", subject = "clickListener")
    public void onSendOrderBtnClick(final ClickEvent<JmixButton> event) {
        OrderDto order = orderGenerator.generate();
        if (order != null) {
            orderGenerator.sendOrder("orders");
            notifications.create("Order: " + order.getInstanceName())
                    .withDuration(5000)
                    .show();
        }
        else
            notifications.create("No parameters")
                    .withDuration(3000)
                    .withType(Notifications.Type.ERROR)
                    .show();

    }

}