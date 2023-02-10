package com.br.exs.consumer.domain;

import java.time.LocalDate;

public class DueDayProcessing {

    private Integer id;
    private LocalDate nextDueDay;
    private Integer customerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getNextDueDay() {
        return nextDueDay;
    }

    public void setNextDueDay(LocalDate nextDueDay) {
        this.nextDueDay = nextDueDay;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public DueDayProcessing(Integer id, LocalDate nextDueDay, Integer customerId) {
        this.id = id;
        this.nextDueDay = nextDueDay;
        this.customerId = customerId;
    }

    public DueDayProcessing() {
    }

}
