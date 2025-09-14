package com.chat.sr.repo;


import com.chat.sr.model.Owner;
import com.chat.sr.model.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VetRepository extends JpaRepository<Vet,Long> {
    Optional<Vet> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

}

