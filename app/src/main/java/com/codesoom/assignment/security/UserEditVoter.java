package com.codesoom.assignment.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.Objects;

/**
 * user 수정/삭제 작업에 접근하는 유저의 권한을 결정한다. <p>
 * return : 요청하는 유저의 userId와 요청하는 url 의 userId가 일치하는 경우 ACCESS_GRANTED, 일치하지 않는 경우 ACCESS_DENIED
 */
public class UserEditVoter implements AccessDecisionVoter<FilterInvocation> {
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation filterInvocation, Collection collection) {
        String requestUrl = filterInvocation.getRequestUrl();

        long userIdFromUrl;
        long userId;
        try {
            userIdFromUrl = Long.parseLong(requestUrl.split("/")[2]);
            userId = (long) authentication.getPrincipal();
        } catch (NumberFormatException e) {
            return ACCESS_DENIED;
        }

        if (Objects.equals(userIdFromUrl, userId)) {
            return ACCESS_GRANTED;
        }
        return ACCESS_DENIED;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }
}
