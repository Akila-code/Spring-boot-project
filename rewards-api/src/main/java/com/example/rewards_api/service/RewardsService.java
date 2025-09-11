package com.example.rewards_api.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rewards_api.exception.CustomerNotFoundException;
import com.example.rewards_api.exception.InvalidTransactionException;
import com.example.rewards_api.model.RewardsSummary;
import com.example.rewards_api.model.Transaction;
import com.example.rewards_api.repository.TransactionRepository;

@Service
public class RewardsService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * All rewards summary (all customers, all time)
     */
    public List<RewardsSummary> calculateRewards() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            throw new InvalidTransactionException("No transactions available");
        }

        return new ArrayList<>(getRewardsSummary(transactions).values());
    }

    /**
     * Specific customer rewards (all time)
     */
    public RewardsSummary calculateCustomerRewards(String customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

        if (transactions.isEmpty()) {
            throw new CustomerNotFoundException("Customer not found or no transactions: " + customerId);
        }

        return getRewardsSummary(transactions).get(customerId);
    }

    /**
     * Specific customer rewards for a given month
     */
    public RewardsSummary calculateCustomerRewardsByMonth(String customerId, String yearMonth) {
        YearMonth targetMonth = YearMonth.parse(yearMonth); // expects "2025-08"
        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        List<Transaction> transactions =
                transactionRepository.findByCustomerIdAndDateBetween(customerId, start, end);

        if (transactions.isEmpty()) {
            throw new InvalidTransactionException("No transactions found for customer " + customerId +
                    " in " + yearMonth);
        }

        return getRewardsSummary(transactions).get(customerId);
    }

    /**
     * All customers rewards for a given month
     */
    public List<RewardsSummary> calculateAllRewardsByMonth(String yearMonth) {
        YearMonth targetMonth = YearMonth.parse(yearMonth);
        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByDateBetween(start, end);

        if (transactions.isEmpty()) {
            throw new InvalidTransactionException("No transactions found in " + yearMonth);
        }

        return new ArrayList<>(getRewardsSummary(transactions).values());
    }

    /**
     * Generate reward summary for given transactions
     */
    private Map<String, RewardsSummary> getRewardsSummary(List<Transaction> transactions) {
        final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MMM");

        Map<String, List<Transaction>> groupedByCustomer =
                transactions.stream().collect(Collectors.groupingBy(Transaction::getCustomerId));

        Map<String, RewardsSummary> summaries = new HashMap<>();

        for (Map.Entry<String, List<Transaction>> entry : groupedByCustomer.entrySet()) {
            String customerId = entry.getKey();
            List<Transaction> customerTxns = entry.getValue();

            String customerName = customerTxns.get(0).getCustomerName();
            Map<String, Integer> monthlyPoints = new HashMap<>();
            int totalPoints = 0;

            for (Transaction txn : customerTxns) {
                int points = calculatePoints(txn.getAmount());
                String monthKey = txn.getDate().format(MONTH_FORMATTER);
                monthlyPoints.put(monthKey, monthlyPoints.getOrDefault(monthKey, 0) + points);
                totalPoints += points;
            }

            RewardsSummary summary = new RewardsSummary();
            summary.setCustomerId(customerId);
            summary.setCustomerName(customerName);
            summary.setMonthlyPoints(monthlyPoints);
            summary.setTotalPoints(totalPoints);

            summaries.put(customerId, summary);
        }

        return summaries;
    }

    /**
     * Calculate points for a transaction amount
     */
    private int calculatePoints(double amount) {
        if (amount <= 0) return 0;

        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2);
            amount = 100;
        }
        if (amount > 50) {
            points += (int) (amount - 50);
        }
        return points;
    }
}
