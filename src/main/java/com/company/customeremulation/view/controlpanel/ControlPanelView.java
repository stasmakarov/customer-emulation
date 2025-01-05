package com.company.customeremulation.view.controlpanel;


import com.company.customeremulation.app.OrderDtoService;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.event.OrderProcessedEvent;
import com.company.customeremulation.event.StoppingOrdersGenerationEvent;
import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.infoboard.OrdersInfo;
import com.company.customeremulation.service.BackgroundOrdersStream;
import com.company.customeremulation.service.OrderGenerator;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.MapDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Map;

@Route(value = "control-panel-view", layout = MainView.class)
@ViewController(id = "cst_ControlPanelView")
@ViewDescriptor(path = "control-panel-view.xml")
public class ControlPanelView extends StandardView {
    private static final Logger log = LoggerFactory.getLogger(ControlPanelView.class);
    private static BackgroundTaskHandler<Void> taskHandler;
    private boolean isOrdersGeneratingOn;

    @Autowired
    private Notifications notifications;
    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private OrderGenerator orderGenerator;
    @Autowired
    private InfoService infoService;
    @Autowired
    private OrderDtoService orderDtoService;

    @ViewComponent
    private JmixButton startBtn;
    @ViewComponent
    private JmixButton stopBtn;
    @ViewComponent
    private Chart orderedItemsChart;
    @ViewComponent
    private Chart ordersPie;
    @ViewComponent
    private H1 totalOrders;
    @ViewComponent
    private VerticalLayout statsPanel;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (taskHandler != null && taskHandler.isAlive()) {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        } else {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }

        if (orderDtoService.getCount() == 0) {
            statsPanel.setVisible(false);
        } else {
            statsPanel.setVisible(true);
            updateCharts();
        }
    }

    private void updateCharts() {
        String value = orderDtoService.getCount().toString();
        totalOrders.setText(value);
        ordersPie.withDataSet(
                new DataSet().withSource(new DataSet.Source<MapDataItem>()
                        .withDataProvider(getOrdersQty())
                        .withCategoryField("category")
                        .withValueField("value"))
        );
        orderedItemsChart.withDataSet(
                new DataSet().withSource(new DataSet.Source<MapDataItem>()
                        .withDataProvider(getItemsQty())
                        .withCategoryField("description")
                        .withValueField("value"))
        );
    }

    @Subscribe(id = "startBtn", subject = "clickListener")
    public void onStartBtnClick(final ClickEvent<JmixButton> event) {
        infoService.initOrderedItemsList();
        BackgroundOrdersStream ordersStream = new BackgroundOrdersStream(60, orderGenerator);
        taskHandler = backgroundWorker.handle(ordersStream);
        taskHandler.execute();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        notifications.create("Orders generation started")
                .withDuration(2000)
                .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
                .show();
        log.info("Orders generation started");
        isOrdersGeneratingOn = true;
    }

    @Subscribe(id = "stopBtn", subject = "clickListener")
    public void onStopBtnClick(final ClickEvent<JmixButton> event) {
        if (taskHandler != null && isOrdersGeneratingOn) {
            taskHandler.cancel();
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            notifications.create("Orders generation stopped")
                    .withThemeVariant(NotificationVariant.LUMO_WARNING)
                    .withDuration(2000)
                    .show();
            log.info("Orders generation stopped");
            isOrdersGeneratingOn = false;
        }
    }

    private ListChartItems<MapDataItem> getItemsQty() {
        List<OrdersInfo> ordersInfos = infoService.getOrdersInfoList();
        ListChartItems<MapDataItem> mapChartItems = new ListChartItems<>();

        for (OrdersInfo ordersInfo : ordersInfos) {
            MapDataItem mapDataItem = new MapDataItem(Map.of("value", ordersInfo.getTotalQty(),
                    "description", ordersInfo.getItemName()));
            mapChartItems.addItem(mapDataItem);
        }
        return mapChartItems;
    }

    private ListChartItems<MapDataItem> getOrdersQty () {
        List<OrdersInfo> ordersInfos = infoService.getOrdersInfoList();
        ListChartItems<MapDataItem> ordersQty = new ListChartItems<>();
        for (OrdersInfo ordersInfo : ordersInfos) {
            MapDataItem mapDataItem = new MapDataItem(Map.of("category", ordersInfo.getItemName(),
                    "value", ordersInfo.getOrdersQty()));
            ordersQty.addItem(mapDataItem);
        }
        return ordersQty;
    }

    @EventListener
    private void onOrderProcessed(OrderProcessedEvent event) {
        OrderDto order = event.getOrder();
        notifications.create(order.getInstanceName())
                .withPosition(Notification.Position.TOP_END)
                .withDuration(5000)
                .show();
        updateCharts();
        statsPanel.setVisible(true);
    }

    @EventListener
    private void onServiceUnavailable(StoppingOrdersGenerationEvent event) {
        if (taskHandler != null && taskHandler.isAlive()) {
            taskHandler.cancel();
            if (taskHandler.isCancelled()) {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            notifications.create("Service unavailable: " + event.getName()
                            + "\nOrders generation stopped")
                    .withThemeVariant(NotificationVariant.LUMO_ERROR)
                    .withDuration(3000)
                    .show();
            log.info("Service unavailable: " + event.getName());
            }
        }
    }
}