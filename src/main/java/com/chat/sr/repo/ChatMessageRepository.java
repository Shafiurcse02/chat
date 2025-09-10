package com.chat.sr.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chat.sr.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT c FROM ChatMessage c " +
            "WHERE (c.sender = :user1 AND c.receiver = :user2) " +
            "   OR (c.sender = :user2 AND c.receiver = :user1) " +
            "ORDER BY c.localDateTime ASC")
    Page<ChatMessage> findPrivateMessagesBetween(
            @Param("user1") String user1,
            @Param("user2") String user2,
            Pageable pageable
    );
    @Query("SELECT m FROM ChatMessage m WHERE m.type = 'CHAT' AND m.receiver IS NULL ORDER BY m.localDateTime ASC")
       Page<ChatMessage> findPublicMessages(Pageable pageable);
}