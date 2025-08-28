package com.example.finalassignment.service;

import com.example.finalassignment.entity.User;
import com.example.finalassignment.repository.UserRepository;
import com.example.finalassignment.security.CustomUserDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService  implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.selectUserByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("user not found");
        }
        return new CustomUserDetail(user);
    }
}