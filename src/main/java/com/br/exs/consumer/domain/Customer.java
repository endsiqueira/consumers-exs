package com.br.exs.consumer.domain;

import java.time.LocalDate;

public class Customer {

    private Integer id;
    private String customerName;
    private Integer dueDay;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getDueDay() {
        return dueDay;
    }

    public void setDueDay(Integer dueDay) {
        this.dueDay = dueDay;
    }

    public Customer(Integer id, String customerName, Integer dueDay) {
        this.id = id;
        this.customerName = customerName;
        this.dueDay = dueDay;
    }

    public Customer() {
    }
}
