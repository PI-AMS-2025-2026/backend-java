package com.fatec.horario.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "day_of_week")
public class DayOfWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private Integer orderNumber;

    public DayOfWeek() {}

    public DayOfWeek(String name, Integer orderNumber) {
        this.name = name;
        this.orderNumber = orderNumber;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
}