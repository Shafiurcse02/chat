package com.chat.sr.repo;


import com.chat.sr.model.FarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmTypeRepository extends JpaRepository<FarmType, Long> {
}
