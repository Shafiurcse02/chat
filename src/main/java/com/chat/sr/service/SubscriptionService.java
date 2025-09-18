package com.chat.sr.service;

import com.chat.sr.model.*;
import com.chat.sr.repo.OwnerRepository;
import com.chat.sr.repo.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final OwnerRepository ownerRepository;

    // নতুন subscription তৈরি
    public Subscription createSubscription(Long ownerId, PlanType planType, Double fee) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate startDate = LocalDate.now();
        // মাসের শেষ দিন বের করা
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Subscription subscription = Subscription.builder()
                .owner(owner)
                .planType(planType)
                .status(SubscriptionStatus.ACTIVE)
                .fee(fee)
                .startDate(startDate)
                .endDate(endDate)
                .nextBillingDate(endDate.plusDays(1)) // পরের মাসের ১ তারিখ
                .build();

        return subscriptionRepository.save(subscription);
    }

    // Renewal (extend subscription)
    public Subscription renewSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // নতুন মাসের শেষ দিন বের করা
        LocalDate newEndDate = subscription.getEndDate().plusMonths(1)
                .withDayOfMonth(subscription.getEndDate().plusMonths(1).lengthOfMonth());

        subscription.setEndDate(newEndDate);
        subscription.setNextBillingDate(newEndDate.plusDays(1));
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        return subscriptionRepository.save(subscription);
    }


    // Cancel subscription
    public Subscription cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.CANCELED);
        return subscriptionRepository.save(subscription);
    }

    // User এর সব subscription খুঁজে আনা
    public List<Subscription> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByOwnerId(userId);
    }
}
