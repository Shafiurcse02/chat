package com.chat.sr.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chat.sr.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	@Query("SELECT cm FROM ChatMessage cm WHERE cm.type='PRIVATE_MESSAGE' AND "
			+ "((cm.sender= :user1 AND cm.recipient= :user2) OR ( cm.sender= :user2 AND cm.recipient= :user1)) "
			+ "ORDER BY cm.localDateTime ASC")
	List<ChatMessage> findPrivateMessageBetweenuser1AndUSer2(@Param("user1") String user1,@Param("user2") String user2);

}
