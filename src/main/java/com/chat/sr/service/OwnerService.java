package com.chat.sr.service;

import com.chat.sr.model.Owner;
import com.chat.sr.repo.OwnerRepository;
import com.chat.sr.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found!"));
    }

    public Owner getOwnerByUserId(Long userId) {
        return ownerRepository.findByUserId(userId).get();
    }
}
