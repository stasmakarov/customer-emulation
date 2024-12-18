package com.company.customeremulation.view.item;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
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

//
//    @Autowired
//    private DataManager dataManager;
//    @ViewComponent
//    private CollectionContainer<ItemDto> itemsDc;
//    @ViewComponent
//    private CollectionLoader<ItemDto> itemsDl;
//
//    @Install(to = "itemsDl", target = Target.DATA_LOADER)
//    protected List<ItemDto> itemsDlLoadDelegate(LoadContext<ItemDto> loadContext) {
//        try {
//            List<ItemDto> entities = dataManager.loadList(loadContext);
//            if (entities == null) {
//                log.error("Received null response from REST endpoint");
//                return Collections.emptyList();
//            } else
//                return entities;
//        } catch (Exception e) {
//            log.error("Error loading data from REST endpoint", e);
//            return Collections.emptyList();
//        }
//    }
//
//
//    @Subscribe
//    public void onBeforeShow(final BeforeShowEvent event) {
//        itemsDl.load();
//    }
//

}
