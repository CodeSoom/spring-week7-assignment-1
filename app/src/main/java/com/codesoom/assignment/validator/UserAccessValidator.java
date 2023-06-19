package com.codesoom.assignment.validator;

import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

public class UserAccessValidator {

	public static void validateAuthorizeUserAccess(UserAuthentication userAuthentication, Long targetUserId) {
		if (!Objects.equals(userAuthentication.getUserId(), targetUserId)) {
			throw new AccessDeniedException("Access Denied : " + targetUserId);
		}
	}

}
