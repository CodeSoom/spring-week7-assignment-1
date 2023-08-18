package com.codesoom.assignment.aop;

import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserNoPermission;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OwnerCheckAspect {
    @Before("@annotation(com.codesoom.assignment.aop.annotation.CheckOwner) && args(id)")
    public void checkOwner(Long id) throws InvalidTokenException, UserNoPermission {
        System.out.println("checkOwner Beforer 전처리 수행");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new InvalidTokenException("AccessToken is null");
        }

        Long loginUserId = (Long) authentication.getPrincipal();

        if(loginUserId != id){
            throw new UserNoPermission("You do not have permission.");
        }

    }
}
