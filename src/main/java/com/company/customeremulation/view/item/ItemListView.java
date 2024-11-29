package com.company.customeremulation.view.item;

import com.company.customeremulation.entity.Item;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.flowui.view.*;

import java.util.Collection;
import java.util.List;

@Route(value = "items", layout = MainView.class)
@ViewController(id = "cst_Item.list")
@ViewDescriptor(path = "item-list-view.xml")
@LookupComponent("itemsDataGrid")
@DialogMode(width = "50em")
public class ItemListView extends StandardListView<Item> {

    @Install(to = "itemsDl", target = Target.DATA_LOADER)
    protected List<Item> itemsDlLoadDelegate(LoadContext<Item> loadContext) {
        // Here you can load entities from an external storage.
        // Set the loaded entities to the not-new state using EntityStates.setNew(entity, false).
        return List.of();
    }

    @Install(to = "itemsDataGrid.remove", subject = "delegate")
    private void itemsDataGridRemoveDelegate(final Collection<Item> collection) {
        for (Item entity : collection) {
            // Here you can remove entities from an external storage
        }
    }
}
