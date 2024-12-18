package com.company.customeremulation.view.controlpanel;


import com.company.customeremulation.infoboard.InfoService;
import com.company.customeremulation.service.BackgroundOrdersStream;
import com.company.customeremulation.service.OrderGenerator;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import io.jmix.flowui.backgroundtask.BackgroundWorker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "control-panel-view", layout = MainView.class)
@ViewController(id = "cst_ControlPanelView")
@ViewDescriptor(path = "control-panel-view.xml")
public class ControlPanelView extends StandardView {
    private static final Logger log = LoggerFactory.getLogger(ControlPanelView.class);
    @Autowired
    private Notifications notifications;

    private static BackgroundTaskHandler<Void> taskHandler;
    private static BackgroundOrdersStream ordersStream;

    @Autowired
    private BackgroundWorker backgroundWorker;
    @Autowired
    private OrderGenerator orderGenerator;
    @ViewComponent
    private JmixButton startBtn;
    @ViewComponent
    private JmixButton stopBtn;
    @Autowired
    private InfoService infoService;
    @Autowired
    private DataManager dataManager;
//    @ViewComponent
//    private Chart itemsOrderedChart;

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

//    @Async
//    @EventListener
//    public void handleCustomEvent(OrderGeneratedEvent event) {
//        notifications.create(event.getOrderDto().getInstanceName())
//                .withPosition(Notification.Position.TOP_END)
//                .show();
//    }

    @Subscribe(id = "startBtn", subject = "clickListener")
    public void onStartBtnClick(final ClickEvent<JmixButton> event) {
        infoService.initItemDtos();
        ordersStream = new BackgroundOrdersStream(60, orderGenerator);
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
}