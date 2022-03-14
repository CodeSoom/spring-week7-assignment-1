package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 사용자를 관리합니다.
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
     * 사용자를 저장하고 반환합니다.
     *
     * @param registrationData 사용자 생성 정보
     * @return 저장된 사용자
     * @throws UserEmailDuplicationException 사용자 이메일이 중복될 경우
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
     * id에 해당하는 사용자를 수정하고 반환합니다.
     *
     * @param id               회원 아이디
     * @param modificationData 수정할 사용자 정보
     * @return 수정된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User updateUser(Long id, UserModificationData modificationData) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);
        user.changePassword(modificationData.getPassword(), passwordEncoder);

        return user;
    }

    /**
     * id에 해당하는 사용자를 삭제하고 반환합니다.
     *
     * @param id 사용자 id
     * @return 저장된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.destroy();
        return user;
    }
}
