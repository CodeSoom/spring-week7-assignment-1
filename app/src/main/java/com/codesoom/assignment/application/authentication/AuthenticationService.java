package com.codesoom.assignment.application.authentication;


import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserRepository;
import com.codesoom.assignment.errors.login.LoginFailException;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService<JwtUtil> {
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//
//    public AuthenticationService(UserRepository userRepository,
//                                 JwtUtil jwtUtil) {
//        this.userRepository = userRepository;
//        this.jwtUtil = jwtUtil;
//    }
//
//    public String login(String email, String password) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new LoginFailException(email));
//
//        if (!user.authenticate(password)) {
//            throw new LoginFailException(email);
//        }
//
//        return jwtUtil.encode(1L);
//    }
//
//    public Long parseToken(String accessToken) {
//        Claims claims = jwtUtil.decode(accessToken);
//        return claims.get("userId", Long.class);
//    }
}
