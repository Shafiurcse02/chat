package com.chat.sr.service;

import com.chat.sr.model.User;

public interface UserService {
	public boolean userExists(String email);
	public void registerUser(User user);
	public void setUserOnlineStatus(String sender, boolean b);

}
