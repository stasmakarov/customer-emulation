package com.company.customeremulation.view.orderdto;

import com.company.customeremulation.app.OrderDtoService;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.util.RemoveOperation;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Route(value = "orderDtoes", layout = MainView.class)
@ViewController(id = "cst_OrderDto.list")
@ViewDescriptor(path = "order-dto-list-view.xml")
@LookupComponent("orderDtoesDataGrid")
@DialogMode(width = "50em")
public class OrderDtoListView extends StandardListView<OrderDto> {

    @Autowired
    private OrderDtoService orderDtoService;
    @ViewComponent
    private CollectionLoader<OrderDto> orderDtoesDl;
    @ViewComponent
    private CollectionContainer<OrderDto> orderDtoesDc;

    @Install(to = "orderDtoesDl", target = Target.DATA_LOADER)
    protected List<OrderDto> orderDtoesDlLoadDelegate(LoadContext<OrderDto> loadContext) {
        return orderDtoService.getOrderDtos();
    }

    @Subscribe(id = "refreshButton", subject = "clickListener")
    public void onRefreshButtonClick(final ClickEvent<JmixButton> event) {
        orderDtoesDl.load();
    }

    @Subscribe(id = "deleteAllButton", subject = "clickListener")
    public void onDeleteAllButtonClick(final ClickEvent<JmixButton> event) {
        orderDtoService.deleteAll();
        orderDtoesDl.load();
    }

}
