package com.chat.sr.controller;

import com.chat.sr.model.*;
import com.chat.sr.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    // Create new subscription
    @PostMapping("/create/{userId}")
    public ResponseEntity<Subscription> createSubscription(
            @PathVariable Long userId,
            @RequestParam PlanType planType,
            @RequestParam Double fee) {

        Subscription subscription = subscriptionService.createSubscription(userId, planType, fee);
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/renew/{subscriptionId}")
    public ResponseEntity<Subscription> renewSubscription(@PathVariable Long subscriptionId) {
        Subscription renewed = subscriptionService.renewSubscription(subscriptionId);
        return ResponseEntity.ok(renewed);
    }


    // Cancel subscription
    @PutMapping("/cancel/{subscriptionId}")
    public ResponseEntity<Subscription> cancelSubscription(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(subscriptionId));
    }

    // Get all subscriptions for a user

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUser(userId);
        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.ok(subscriptions);
    }

}
