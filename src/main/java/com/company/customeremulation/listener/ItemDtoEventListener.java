package com.company.customeremulation.listener;

import com.company.customeremulation.entity.ItemDto;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("cst_ItemDtoEventListener")
public class ItemDtoEventListener {

    @EventListener
    public void onItemDtoSaving(final EntitySavingEvent<ItemDto> event) {
        ItemDto entity = event.getEntity();
        System.out.println("Item Listener");
    }
}