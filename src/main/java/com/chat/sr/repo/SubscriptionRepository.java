package com.chat.sr.repo;

import com.chat.sr.model.Owner;
import com.chat.sr.model.Subscription;
import com.chat.sr.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByOwnerId(Long ownerId);
    List<Subscription> findByStatus(SubscriptionStatus status);

    List<Subscription> findByOwner(Owner owner);
    List<Subscription> findByOwnerAndStatus(Owner owner, SubscriptionStatus status);
    boolean existsByOwnerAndStatus(Owner owner, SubscriptionStatus status);
}
