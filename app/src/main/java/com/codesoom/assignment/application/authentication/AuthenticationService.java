package com.codesoom.assignment.application.authentication;

import com.codesoom.assignment.UserNotFoundException;
import com.codesoom.assignment.application.user.UserQueryService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "Email[" + email + "] 에 해당하는 사용자를 찾을 수 없어 로그인을 할 수 없습니다."
                        )
                );
        return jwtUtil.encode(user.getId());
    }

}
