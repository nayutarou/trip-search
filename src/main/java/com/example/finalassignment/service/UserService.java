package com.example.finalassignment.service;

import com.example.finalassignment.entity.User;
import com.example.finalassignment.form.UserForm;
import com.example.finalassignment.mapper.UserMapper;
import com.example.finalassignment.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserForm userform){

        if (userRepository.selectUserByEmail(userform.getEmail()) != null){
            // 既にユーザーが存在する場合、例外をスローする
            // このメッセージがControllerのcatchブロックで受け取られる
            throw new DuplicateKeyException("このメールアドレスは既に使用されています。");
        }

        User user = new User();
        user.setUsername(userform.getEmail());
        user.setEmail(userform.getEmail()); // ← ★この行を追加してください
        String hashedPassword = passwordEncoder.encode(userform.getPassword());
        user.setPassword(hashedPassword);
        userRepository.insertUser(user);
    }

}

