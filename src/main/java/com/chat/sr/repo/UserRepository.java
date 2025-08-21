package com.chat.sr.repo;

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

	@Transactional
	@Modifying
	@Query("Update user u set u.isOnline=:isOnline where u.userName=:userName")
	public void updateUserOnlineStatus(@Param("userName") String userName, @Param("isOnline") boolean isOnline);

}
