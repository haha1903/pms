package com.datayes.invest.pms.service.order;

import java.util.ArrayList;
import java.util.List;

public class OrderBasket {

    private Long id;

    private List<Order> orders = new ArrayList<>();

    public OrderBasket() {
    }

    public OrderBasket(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
