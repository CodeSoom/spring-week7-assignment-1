package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.UserModificationData;

/**
 * 사용자 정보를 수정합니다.
 */
public interface UserModifiable {
    User updateUser(Long id, UserModificationData modificationData);
    User deleteUser(Long id);
}
