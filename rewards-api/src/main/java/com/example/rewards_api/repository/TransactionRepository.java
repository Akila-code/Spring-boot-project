package com.example.rewards_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rewards_api.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Find all transactions by customerId
    List<Transaction> findByCustomerId(String customerId);

    // Find all transactions for a customer in a date range
    List<Transaction> findByCustomerIdAndDateBetween(String customerId, LocalDate start, LocalDate end);

    // Find all transactions in a date range (all customers)
    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);
}
