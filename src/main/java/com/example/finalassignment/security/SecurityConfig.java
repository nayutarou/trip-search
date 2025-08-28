package com.example.finalassignment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception{
        http
                // 認可
                .authorizeHttpRequests(request -> request
                        // どの(any)リクエストにも認証(authenticated)してないといけないという意味
                        .requestMatchers("/register","/login","/error","/css/**","/img/**").permitAll()
                        .anyRequest().authenticated())
                // csrf設定
                .csrf(csrf ->csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))

                // 認証
                .formLogin(login -> login
                        // postで送る場所
                        .loginProcessingUrl("/login")
                        // get送る場所
                        .loginPage("/login")
                        // login成功時
                        .usernameParameter("email")
                        .passwordParameter("password")

                        .defaultSuccessUrl("/spots")
                        // login失敗時
                        .failureUrl("/login?error")
                        // 上記のことに対して、全て許可する
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
