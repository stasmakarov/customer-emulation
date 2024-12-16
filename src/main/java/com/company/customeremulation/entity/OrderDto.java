package com.company.customeremulation.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.util.UUID;

@JmixEntity(name = "cst_OrderDto")
public class OrderDto {
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    private String customer;

    private String address;

    private ItemDto itemDto;

    private ItemPersisted itemPersisted;

    private Integer quantity;

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ItemPersisted getItemPersisted() {
        return itemPersisted;
    }

    public void setItemPersisted(ItemPersisted itemPersisted) {
        this.itemPersisted = itemPersisted;
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

    public String getInstanceName() {
        return String.format("%s, %s: %s - %s",
                customer,
                address,
                itemPersisted.getName(),
                quantity.toString());
    }
}