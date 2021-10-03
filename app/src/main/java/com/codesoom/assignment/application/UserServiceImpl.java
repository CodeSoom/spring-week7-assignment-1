package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserData;
import com.codesoom.assignment.dto.UserUpdateData;
import com.codesoom.assignment.errors.UserEmailDuplicateException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(UserData source) throws Exception {

        if( isEmailDuplicated(source.getEmail()) ) {
            throw new UserEmailDuplicateException();
        }

        User user = User.builder()
                .name(source.getName())
                .email(source.getEmail())
                .build();
        user.userUpdatePassword(source.getPassword());

        return userRepository.save(user);

    }

    @Override
    public User getUser(Long id) {

       return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());

    }

    @Override
    public User updateUser(Long id, UserUpdateData userUpdateData) {

        User findUser = getUser(id);

        findUser.userUpdateName(userUpdateData.getName());
        findUser.userUpdatePassword(userUpdateData.getPassword());

        return findUser;

    }

    @Override
    public void deleteUser(Long id) {

        User deleteUser = getUser(id);

        userRepository.deleteById(id);

    }

    @Override
    public boolean isEmailDuplicated(String mail) {
        return userRepository.existsByEmail(mail);
    }

}
