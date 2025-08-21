package com.chat.sr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.sr.model.User;
import com.chat.sr.repo.UserRepository;

@Service
public class UserServiceImp implements UserService  {

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean userExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public void registerUser(User user) {
		if (userExists(user.getEmail())) {
			throw new RuntimeException("User already exists with this email!");
		}
		userRepository.save(user);
		
	}

	@Override
	public void setUserOnlineStatus(String sender, boolean isActive) {
		userRepository.updateUserOnlineStatus(sender,isActive);
		
	}

}