package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserInquiryInfo;
import com.codesoom.assignment.dto.UserRegisterData;
import org.springframework.stereotype.Service;

@Service
class NormalUserService implements UserService {
    private final UserRepository userRepository;

    public NormalUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserInquiryInfo register(UserRegisterData registerData) {
        User user = userRepository.save(registerData.toUser());
        return UserInquiryInfo.from(user);
    }
}
