package com.chat.sr.service;

import com.chat.sr.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.sr.model.User;
import com.chat.sr.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    public List<User> findAllUsers() {
        return  userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username).orElse(null);
    }

    @Override
    public User getUserByUId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> usersWithRole(String role) {
        try {
            Role enumRole = Role.valueOf(role.toUpperCase()); // String → Enum
            return userRepository.findByRole(enumRole);
        } catch (IllegalArgumentException e) {
            // যদি অবৈধ role পাঠানো হয়
            return Collections.emptyList();
        }
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