package com.company.customeremulation.view.item;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.restds.impl.RestClientCredentialsAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Route(value = "items", layout = MainView.class)
@ViewController(id = "cst_Item.list")
@ViewDescriptor(path = "item-list-view.xml")
@LookupComponent("itemsDataGrid")
@DialogMode(width = "50em")
public class ItemListView extends StandardListView<ItemDto> {
    private static final Logger log = LoggerFactory.getLogger(ItemListView.class);

    @Autowired
    private RestClientCredentialsAuthenticator authenticator;
    @Autowired
    private Dialogs dialogs;

    @Subscribe
    public void onInit(final InitEvent event) {
        try {
            String token = authenticator.getAuthenticationToken();
            System.out.println(token);
        } catch (Exception e) {
            dialogs.createMessageDialog()
                    .withText("REST Data Store is unavailable")
                    .withCloseOnEsc(true)
                    .withCloseOnOutsideClick(true)
                    .build()
                    .open();
            this.closeWithDiscard();
        }
    }

}
