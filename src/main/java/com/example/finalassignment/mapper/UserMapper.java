package com.example.finalassignment.mapper;

import com.example.finalassignment.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT user_id,username,password,created_at FROM users WHERE email = #{email}")
    User selectUserByName(String username);

    @Select("SELECT user_id,username,password,created_at FROM users WHERE email = #{email}")
    User selectUserByEmail(String email);

    @Insert("INSERT INTO users (email,password) VALUES (#{email},#{password})")
    @Options(useGeneratedKeys = true,keyProperty = "userId")
    void insertUser(User user);

    /**
     * ★★★ 複数のIDを指定して、ユーザーリストを取得する ★★★
     * SpotServiceで実際に必要なのはこちらのメソッドです。
     * @param userIds 検索したいユーザーIDのリスト
     * @return ユーザーのリスト
     */
    @Select("""
        <script>
            SELECT * FROM users
            WHERE user_id IN
            <foreach item='id' collection='userIds' open='(' separator=',' close=')'>
                #{id}
            </foreach>
        </script>
    """)
    List<User> findByIds(@Param("userIds") List<Long> userIds);

    @Select("SELECT * FROM users")
    List<User> findAll();

}
