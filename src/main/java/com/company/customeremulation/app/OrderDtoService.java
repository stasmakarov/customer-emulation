package com.company.customeremulation.app;

import com.company.customeremulation.entity.OrderDto;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("cst_OrderDtoService")
public class OrderDtoService {

    private ArrayList<OrderDto> orderDtos;

    public OrderDtoService() {
        this.orderDtos = new ArrayList<>();
    }

    public ArrayList<OrderDto> getOrderDtos() {
        return orderDtos;
    }

    public void setOrderDtos(ArrayList<OrderDto> orderDtos) {
        this.orderDtos = orderDtos;
    }

    public void addOrderDto(OrderDto exampleDto) {
        orderDtos.add(exampleDto);
    }

    public Integer getCount() {
        return orderDtos.size();
    }

    public void deleteAll() {
        orderDtos.clear();
    }
}