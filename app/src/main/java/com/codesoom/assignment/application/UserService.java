package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserCreateData;
import com.codesoom.assignment.dto.UserResultData;
import com.codesoom.assignment.dto.UserUpdateData;
import com.codesoom.assignment.errors.UserEmailDuplicatedException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/** 사용자에 대한 요청을 수행한다. */
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
     * 전체 사용자 목록을 리턴한다.
     *
     * @return 저장되어 있는 전체 사용자 목록
     */
    public List<UserResultData> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResultData::of)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 식별자에 해당하는 사용자를 리턴한다.
     *
     * @param id - 조회하고자 하는 사용자의 식별자
     * @return 주어진 {@code id}에 해당하는 사용자
     * @throws UserNotFoundException 만약
     *         주어진 {@code id}에 해당하는 사용자가 저장되어 있지 않은 경우
     */
    public UserResultData getUser(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .map(UserResultData::of)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * 주어진 사용자를 저장하고 해당 사용자를 리턴한다.
     *
     * @param userCreateData - 저장하고자 하는 새로운 사용자
     * @return 저장 된 사용자
     * @throws UserEmailDuplicatedException 만약
     *         주어진 이메일이 이미 존재하는 경우
     */
    public UserResultData createUser(UserCreateData userCreateData) {
        String email = userCreateData.getEmail();
        if(userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicatedException(email);
        }

        User user = mapper.map(userCreateData, User.class);
        user.updatePassword(userCreateData.getPassword(), passwordEncoder);
        User savedUser = userRepository.save(user);

        return UserResultData.of(savedUser);
    }

    /**
     * 주어진 식별자에 해당하는 서용자를 수정하고 해당 사용자를 리턴한다.
     *
     * @param id - 수정하고자 하는 사용자의 식별자
     * @param userUpdateData - 수정하고자 하는 새로운 사용자
     * @return 수정 된 사용자
     * @throws UserNotFoundException 만약
     *         주어진 {@code id}에 해당하는 사용자가 저장되어 있지 않은 경우
     */
    public UserResultData updateUser(Long id, UserUpdateData userUpdateData) {
        UserResultData userResultData = getUser(id);

        User updatedUser = mapper.map(userResultData, User.class);
        updatedUser.updateName(userUpdateData.getName());
        updatedUser.updatePassword(userUpdateData.getPassword(), passwordEncoder);

        return UserResultData.of(updatedUser);
    }

    /**
     * 주어진 식별자에 해당하는 사용자를 삭제하고 해당 사용자를 리턴한다.
     *
     * @param id - 삭제하고자 하는 사용자의 식별자
     * @return 삭제 된 사용자
     * @throws UserNotFoundException 만약
     *          주어진 {@code id}에 해당하는 사용자가 저장되어 있지 않은 경우
     */
    public UserResultData deleteUser(Long id) {
        UserResultData userResultData = getUser(id);

        User deleteUser = mapper.map(userResultData, User.class);
        deleteUser.delete();

        return UserResultData.of(deleteUser);
    }
}
