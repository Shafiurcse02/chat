package com.chat.sr.service;

import com.chat.sr.controller.AppointmentController;
import com.chat.sr.dto.AppointmentDTO;
import com.chat.sr.dto.OwnerDTO;
import com.chat.sr.dto.PetsDTO;
import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.Owner;
import com.chat.sr.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.sr.model.User;
import com.chat.sr.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService  {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

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
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .district(user.getDistrict())
                .thana(user.getThana())
                .po(user.getPo())
                .photo(user.getPhoto())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();

        Owner owner = user.getOwner();
        if (owner != null) {
            OwnerDTO ownerDTO = OwnerDTO.builder()
                    .id(owner.getId())
                    .firmName(owner.getFirmName())
                    .appointments(owner.getAppointments()
                            .stream()
                            .map(a -> AppointmentDTO.builder()
                                    .id(a.getId())
                                    .description(a.getDescription())
                                    .species(a.getSpecies())
                                    .gender(a.getGender())
                                    .age(a.getAge())
                                    .appointmentAt(a.getAppointmentAt())
                                    .appointmentDate(a.getAppointmentDate())
                                    .ownerId(owner.getId())
                                    .vetId(a.getVet() != null ? a.getVet().getId() : null)
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            userDTO.setOwner(ownerDTO);
        }

        return userDTO;
    }
}