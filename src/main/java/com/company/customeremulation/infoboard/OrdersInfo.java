package com.company.customeremulation.infoboard;

public class OrdersInfo {
    private String itemName;
    private Integer ordersQty;
    private Integer totalQty;

    public OrdersInfo(String itemName, Integer ordersQty, Integer totalQty) {
        this.itemName = itemName;
        this.ordersQty = ordersQty;
        this.totalQty = totalQty;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getOrdersQty() {
        return ordersQty;
    }

    public void setOrdersQty(Integer ordersQty) {
        this.ordersQty = ordersQty;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }
}
