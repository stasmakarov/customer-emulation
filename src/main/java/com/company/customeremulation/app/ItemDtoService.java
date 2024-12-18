package com.company.customeremulation.app;

import com.company.customeremulation.entity.ItemDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("cst_ItemDtoService")
public class ItemDtoService {

    private List<ItemDto> itemDtos;

    public ItemDtoService() {
        this.itemDtos = new ArrayList<>();
    }

    public List<ItemDto> getItemDtos() {
        return itemDtos;
    }

    public void setItemDtos(List<ItemDto> itemDtos) {
        this.itemDtos = itemDtos;
    }
}