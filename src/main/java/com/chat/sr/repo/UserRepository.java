package com.chat.sr.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chat.sr.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);
    Optional<User> findByUserName(String userName);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.farms WHERE u.id = :id")
    User findByIdWithFarms(@Param("id") Long id);
   @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.isActive = :status WHERE u.userName = :username")
    void updateUserIsActiveStatus(@Param("username") String userName,
                                @Param("status") boolean status);
    List<User> findAllByIsActiveTrue(); // ✅ online user list


}
