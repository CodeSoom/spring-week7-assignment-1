package com.codesoom.assignment.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.Objects;

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
