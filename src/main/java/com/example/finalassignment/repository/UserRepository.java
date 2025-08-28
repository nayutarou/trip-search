package com.example.finalassignment.repository;

import com.example.finalassignment.entity.User;
import com.example.finalassignment.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final UserMapper userMapper;

    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User selectUserByUsername(String username){
        return userMapper.selectUserByName(username);
    }

    public User selectUserByEmail(String email){
        return userMapper.selectUserByEmail(email);
    }

    public void insertUser(User user){
        userMapper.insertUser(user);
    }

    /**
     * ★★★ ここを修正 ★★★
     * 複数のIDを指定して、ユーザーのリストを取得します。
     * @param userIds 検索したいユーザーIDのリスト
     * @return ユーザーのリスト
     */
    public List<User> findByIds(List<Long> userIds) {
        // userIdsが空の場合は、DBに問い合わせずに空のリストを返す
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        // Mapperのメソッドを呼び出し、結果を返す
        return userMapper.findByIds(userIds);
    }

}

