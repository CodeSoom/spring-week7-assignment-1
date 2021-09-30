package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserLoginData;
import com.codesoom.assignment.errors.LoginInconsistencyException;
import com.codesoom.assignment.errors.UnauthorizedException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 로그인 요청 정보를 받아 로그인 처리 후, 인증 토큰을 리턴합니다.
     * @param loginData 로그인 요청
     * @return 인증 토큰
     * @throws LoginInconsistencyException 로그인 실패할 경우
     */
    public String login(UserLoginData loginData) {

        User user = findUser(loginData);
        if(!user.getPassword().equals(loginData.getPassword())) {
            throw new LoginInconsistencyException();
        }
        
        return jwtUtil.encode(user.getId());
    }

    /**
     * 유저 토큰 정보를 받아 복호화 처리 후, 유저 아이디를 리턴합니다.
     * @param accessToken 토큰 요청
     * @return 유저 아이디
     * @throws UnauthorizedException 유효하지 않은 토큰일 경우
     */
    public Long parseToken(String accessToken) {

        if(accessToken==null || accessToken.isBlank()) {
            throw new UnauthorizedException();
        }

        try {
            Claims decode = jwtUtil.decode(accessToken);
            Long userId = decode.get("userId", Long.class);
            return userId;
        }catch (SignatureException e) {
            throw new UnauthorizedException();
        }

     }

    /**
     * 로그인 요청에 들어온 유저가 존재하는지 확인 후, 유저 정보를 리턴합니다.
     * @param loginData 로그인 요청
     * @return 유저 정보
     * @throws UserNotFoundException 유저를 찾지 못할 경우
     */
    public User findUser(UserLoginData loginData) {

        return userRepository.findByEmail(loginData.getEmail()).orElseThrow(() -> new UserNotFoundException());

    }

}
