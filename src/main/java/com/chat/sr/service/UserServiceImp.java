package com.chat.sr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.sr.model.User;
import com.chat.sr.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImp implements UserService  {

	@Autowired
	private UserRepository userRepository;

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUserName(username).isPresent();
    }
	@Override
	public void registerUser(User user) {
		if (userExists(user.getEmail())) {
			throw new RuntimeException("User already exists with this email!");
		}
		userRepository.save(user);
		
	}


    @Override
    public void setUserOIsActiveStatus(String username, boolean status) {
        userRepository.findByUserName(username).ifPresent(user -> {
            userRepository.updateUserIsActiveStatus(user.getUserName(),status);

            System.out.println((status ? "🟢" : "🔴") + " User " + username + " is now " + (status ? "online" : "offline"));
        });
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username).orElse(null);
    }
    @Transactional(readOnly = true)
    @Override
    public List<String> getOnlineUsers() {
        return userRepository.findAllByIsActiveTrue()
                .stream()
                .map(User::getUserName)
                .toList();
    }


}