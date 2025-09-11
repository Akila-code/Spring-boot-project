package com.example.rewards_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.rewards_api.model.RewardsSummary;
import com.example.rewards_api.service.RewardsService;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;

    /**
     * 1. All customers summary (overall)
     */
    @GetMapping
    public List<RewardsSummary> getAllRewards() {
        return rewardsService.calculateRewards();
    }

    /**
     * 2. Specific customer summary (overall)
     */
    @GetMapping("/{customerId}")
    public RewardsSummary getCustomerRewards(@PathVariable String customerId) {
        return rewardsService.calculateCustomerRewards(customerId);
    }

    /**
     * 3. Specific customer summary for a given month (yyyy-MM)
     */
    @GetMapping("/{customerId}/{yearMonth}")
    public RewardsSummary getCustomerRewardsByMonth(
            @PathVariable String customerId,
            @PathVariable String yearMonth) {
        return rewardsService.calculateCustomerRewardsByMonth(customerId, yearMonth);
    }

    /**
     * 4. All customers summary for a given month (yyyy-MM)
     */
    @GetMapping("/month/{yearMonth}")
    public List<RewardsSummary> getAllRewardsByMonth(@PathVariable String yearMonth) {
        return rewardsService.calculateAllRewardsByMonth(yearMonth);
    }
}
