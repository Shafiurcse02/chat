package com.chat.sr.service;

import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.User;

import java.util.List;

public interface UserService {
	public void registerUser(User user);

     List<String> getOnlineUsers();

    boolean userExists(String username);

    void setUserOIsActiveStatus(String username, boolean status);

    public List<User> findAllUsers();
    User getUserByUsername(String username);
    User getUserByUId(Long  id);
    List<User> usersWithRole(String role);
    public List<UserDTO> getAllUsers();


}
