package com.company.customeremulation.view.item;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.LoadContext;
import io.jmix.core.SaveContext;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.view.*;

import java.util.Set;

@Route(value = "items/:id", layout = MainView.class)
@ViewController(id = "cst_Item.detail")
@ViewDescriptor(path = "item-detail-view.xml")
@EditedEntityContainer("itemDc")
public class ItemDetailView extends StandardDetailView<ItemDto> {
}
