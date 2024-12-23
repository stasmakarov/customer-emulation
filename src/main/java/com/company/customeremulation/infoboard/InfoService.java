package com.company.customeremulation.infoboard;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.repository.ItemDtoRepository;
import com.vaadin.flow.data.provider.DataProvider;
import io.jmix.chartsflowui.data.item.SimpleDataItem;
import io.jmix.chartsflowui.kit.data.chart.ListChartItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("cst_InfoService")
public class InfoService {

    private static final Logger log = LoggerFactory.getLogger(InfoService.class);
    private final List<OrderedValue> orderedItems;

    @Autowired
    private ItemDtoRepository itemDtoRepository;

    public InfoService() {
        orderedItems = new ArrayList<>();
    }

    public void initOrderedItemsList() {
        for (ItemDto itemDto : itemDtoRepository.findAll()) {
            OrderedValue orderedItem = new OrderedValue(itemDto.getName(), 0);
            orderedItems.add(orderedItem);
        }
        log.info("Init ordered items list");
    }

    public void resetOrderedItemsMap() {
    }

    public void countOrder(OrderDto order) {
        String name = order.getItemDto().getName();
        Optional<OrderedValue> foundItem = orderedItems.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();

        if (foundItem.isPresent()) {
            Integer prevValue = foundItem.get().getValue();
            if (prevValue != null) {
                Integer nextValue = prevValue + order.getQuantity();
                foundItem.get().setValue(nextValue);
            }
            System.out.println("Found item: " + foundItem.get());
        } else {
            System.out.println("No item found with name: " + name);
        }
    }

    public DataProvider<SimpleDataItem, ?> getOrderedItems() {
        return null;
    }
}