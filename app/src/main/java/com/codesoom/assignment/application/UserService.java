package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.ForbiddenRequestException;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 회원을 생성, 수정, 삭제합니다.
 */
@Service
@Transactional
public class UserService {
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(Mapper dozerMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mapper = dozerMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * 회원을 생성하여 리턴합니다.
     * @param registrationData 회원 생성 정보
     * @return 생성된 회원
     * @throws UserEmailDuplicationException 회원 이메일이 중복되는 경우
     */
    public User registerUser(UserRegistrationData registrationData) {
        String email = registrationData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = mapper.map(registrationData, User.class);

        // TODO: 로직 변경하기
        user.changePassword(registrationData.getPassword(), passwordEncoder);

        return userRepository.save(user);
    }

    /**
     * 해당 식별자의 회원을 수정하여 리턴합니다.
     * @param id 회원 식별자
     * @param modificationData 회원 수정 정보
     * @return 수정된 회원
     * @throws ForbiddenRequestException 해당 식별자의 회원을 찾을 수 없는 경우
     */
    public User updateUser(Long id, UserModificationData modificationData) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new ForbiddenRequestException());

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);
        user.changePassword(modificationData.getPassword(), passwordEncoder);

        return user;
    }

    /**
     * 해당 식별자의 회원을 삭제하여 리턴합니다.
     * @param id 회원 식별자
     * @return 삭제된 회원
     * @throws UserNotFoundException 해당 식별자의 회원을 찾을 수 없는 경우
     */
    public User deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.destroy();
        return user;
    }
}
