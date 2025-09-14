package com.chat.sr.repo;


import com.chat.sr.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    // Optional: find Owner by associated User (if you need this)
    Optional<Owner> findByUserId(Long userId);

}