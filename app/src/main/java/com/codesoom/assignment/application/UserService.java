package com.codesoom.assignment.application;

import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * 신규 회원을 등록하고, 등록된 회원정보를 리턴한다.
     * @param command 신규 회원정보
     * @return 등록된 회원정보
     */
    public User registerUser(UserCommand.Register command) {
        final String email = command.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        final User user = userMapper.toEntity(command);
        return userRepository.save(user);
    }

    /**
     * 회원정보를 수정하고, 수정된 회원정보를 리턴한다.
     * @param command 수정 회원정보
     * @return 수정된 상품정보
     * @throws UserNotFoundException 회원을 찾지 못한 경우
     */
    public User updateUser(UserCommand.Update command) {
        final User user = findUser(command.getId());

        user.modifyUserInfo(userMapper.toEntity(command));

        return user;
    }

    /**
     * 회원정보를 삭제하고, 삭제된 회원정보를 리턴한다.
     * @param id 삭제할 회원 ID
     * @return 삭제된 회원정보
     * @throws UserNotFoundException 회원을 찾지 못한 경우
     */
    public User deleteUser(Long id) {
        final User user = findUser(id);
        user.deleteUser();
        return user;
    }

    private User findUser(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
