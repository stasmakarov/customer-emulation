package com.company.customeremulation.view.controlpanel;


import com.company.customeremulation.event.OrderGeneratedEvent;
import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.service.BackgroundOrdersStream;
import com.company.customeremulation.service.OrderGenerator;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.SimpleDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.event.TransactionalEventListener;

@Route(value = "control-panel-view", layout = MainView.class)
@ViewController(id = "cst_ControlPanelView")
@ViewDescriptor(path = "control-panel-view.xml")
public class ControlPanelView extends StandardView {
    private static final Logger log = LoggerFactory.getLogger(ControlPanelView.class);
    private static BackgroundTaskHandler<Void> taskHandler;

    @Autowired
    private Notifications notifications;
    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private OrderGenerator orderGenerator;
    @Autowired
    private InfoService infoService;

    @ViewComponent
    private JmixButton startBtn;
    @ViewComponent
    private JmixButton stopBtn;
    @ViewComponent
    private Chart orderedItemsChart;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (taskHandler != null && taskHandler.isAlive()) {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        } else {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }
//        orderedItemsChart.withDataSet(
//                new DataSet().withSource(new DataSet.Source<SimpleDataItem>()
//                        .withDataProvider(infoService.getOrderedItems())
//                        .withCategoryField("description")
//                        .withValueField("value"))
//        );
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
    }

    @Subscribe(id = "stopBtn", subject = "clickListener")
    public void onStopBtnClick(final ClickEvent<JmixButton> event) {
        if (taskHandler != null) {
            taskHandler.cancel();
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            notifications.create("Orders generation stopped")
                    .withThemeVariant(NotificationVariant.LUMO_WARNING)
                    .withDuration(2000)
                    .show();
            log.info("Orders generation stopped");
        }
    }


}