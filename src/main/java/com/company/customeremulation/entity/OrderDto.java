package com.company.customeremulation.entity;

import com.company.customeremulation.rabbit.OrderDtoSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.util.UUID;

@JmixEntity(name = "cst_OrderDto")
@JsonSerialize(using = OrderDtoSerializer.class)
public class OrderDto {
    @JmixGeneratedValue
    @JmixId
    @JsonIgnore
    private UUID id;

    private String customer;

    private String address;

    private ItemDto itemDto;

    private Integer quantity;

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ItemDto getItemDto() {
        return itemDto;
    }

    public void setItemDto(ItemDto itemDto) {
        this.itemDto = itemDto;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @JsonIgnore
    public String getInstanceName() {
        return String.format("%s, %s: %s - %s",
                customer,
                address,
                itemDto.getName(),
                quantity.toString());
    }
}