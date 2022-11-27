package com.codesoom.assignment.session.application;

import com.codesoom.assignment.common.utils.JwtUtil;
import com.codesoom.assignment.session.application.exception.LoginFailException;
import com.codesoom.assignment.session.domain.Role;
import com.codesoom.assignment.session.domain.RoleRepository;
import com.codesoom.assignment.user.domain.User;
import com.codesoom.assignment.user.domain.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(final UserRepository userRepository,
                                 final RoleRepository roleRepository,
                                 final JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(final String email,
                        final String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LoginFailException(email));

        if (!user.authenticate(password)) {
            throw new LoginFailException(email);
        }

        return jwtUtil.encode(user.getId());
    }

    public Long parseToken(final String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }

    public List<Role> roles(Long userId) {
        return roleRepository.findAllByUserId(userId);
    }
}
