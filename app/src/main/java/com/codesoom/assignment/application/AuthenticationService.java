package com.codesoom.assignment.application;

import com.codesoom.assignment.application.dto.SessionCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 로그인 정보로 인증 후 인증토큰을 리턴한다.
     * @param command 로그인 정보
     * @return 인증토큰
     * @throws LoginFailException 로그인 인증에 실패한 경우
     */
    public String login(SessionCommand.SessionRequest command) {
        final User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new LoginFailException(command.getEmail()));

        if (!user.authenticate(command.getPassword(), passwordEncoder)) {
            throw new LoginFailException(command.getEmail());
        }

        return jwtTokenProvider.createToken(user.getId());
    }

}
