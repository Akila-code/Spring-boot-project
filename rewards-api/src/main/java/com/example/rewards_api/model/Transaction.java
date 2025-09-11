package com.example.rewards_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // Primary key (auto-generated)

    private String customerId;

    private String customerName;

    private LocalDate date;

    private double amount;

    public Transaction() {
        // Default constructor required by JPA
    }

    public Transaction(String customerId, String customerName, LocalDate date, double amount) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.date = date;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
