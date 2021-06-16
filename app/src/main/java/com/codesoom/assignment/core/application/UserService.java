package com.codesoom.assignment.core.application;

import com.codesoom.assignment.core.domain.User;
import com.codesoom.assignment.core.domain.UserRepository;
import com.codesoom.assignment.web.dto.UserModificationData;
import com.codesoom.assignment.web.dto.UserRegistrationData;
import com.codesoom.assignment.error.UserEmailDuplicationException;
import com.codesoom.assignment.error.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 회원 데이터를 가공하여 반환하거나 처리합니다.
 */
@Service
@Transactional
public class UserService {
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(Mapper dozerMapper,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.mapper = dozerMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 신규 회원을 등록합니다.
     * @param registrationData 등록할 회원 데이터
     * @return 등록한 회원
     */
    public User registerUser(UserRegistrationData registrationData) {
        String email = registrationData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = mapper.map(registrationData, User.class);

        user.changePassword(registrationData.getPassword(), passwordEncoder);

        return userRepository.save(user);
    }

    /**
     * 회원 정보를 갱신합니다.
     * @param id 회원 식별자
     * @param modificationData 갱신할 회원 데이터
     * @return 갱신한 회원
     */
    public User updateUser(Long id, UserModificationData modificationData) {
        User user = findUser(id);

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);

        return user;
    }

    /**
     * 회원을 삭제 처리합니다. 회원의 deleted 상태를 true 값으로 변경하는 것을 의미합니다.
     * @param id 회원 식별자
     * @return 삭제 처리한 회원
     */
    public User deleteUser(Long id) {
        User user = findUser(id);
        user.destroy();
        return user;
    }

    /**
     * ID에 해당하는 회원를 찾습니다.
     * @param id 회원 식별자
     * @return 회원
     * @throws UserNotFoundException ID가 null이거나 해당 회원이 없을 경우.
     */
    private User findUser(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
