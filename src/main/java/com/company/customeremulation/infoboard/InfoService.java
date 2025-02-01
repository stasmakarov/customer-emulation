package com.company.customeremulation.infoboard;

import com.company.customeremulation.entity.ItemDto;
import com.company.customeremulation.entity.OrderDto;
import com.company.customeremulation.repository.ItemDtoRepository;
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
    private final List<OrdersInfo> ordersInfoList;

    @Autowired
    private ItemDtoRepository itemDtoRepository;

    public InfoService() {
        ordersInfoList = new ArrayList<>();
    }

    public List<OrdersInfo> getOrdersInfoList() {
        return ordersInfoList;
    }

    public boolean initOrderedItemsList() {
        try {
            for (ItemDto itemDto : itemDtoRepository.findAll()) {
                OrdersInfo orderedItem = new OrdersInfo(itemDto.getName(), 0,0);
                ordersInfoList.add(orderedItem);
            }
            log.info("Init ordered items list");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void countOrder(OrderDto order) {
        String name = order.getItemDto().getName();
        Optional<OrdersInfo> foundItem = ordersInfoList.stream()
                .filter(item -> item.getItemName().equals(name))
                .findFirst();

        if (foundItem.isPresent()) {
            Integer prevValue = foundItem.get().getTotalQty();
            if (prevValue != null) {
                Integer nextOrdersQty = foundItem.get().getOrdersQty() + 1;
                Integer nextValue = prevValue + order.getQuantity();
                foundItem.get().setOrdersQty(nextOrdersQty);
                foundItem.get().setTotalQty(nextValue);
            }
            System.out.println("Found item: " + foundItem.get());
        } else {
            System.out.println("No item found with name: " + name);
        }
    }

}