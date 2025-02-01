package com.company.customeremulation.view.item;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.service.RestServiceChecker;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import io.jmix.restds.impl.RestClientCredentialsAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "items", layout = MainView.class)
@ViewController(id = "cst_Item.list")
@ViewDescriptor(path = "item-list-view.xml")
@LookupComponent("itemsDataGrid")
@DialogMode(width = "50em")
public class ItemListView extends StandardListView<ItemDto> {
}
