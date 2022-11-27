package com.codesoom.assignment.common.authorization;

import com.codesoom.assignment.common.authentication.security.UserAuthentication;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;

@Aspect
@Component
public class UserAuthorizationAop {
    /**
     * `@IdVerification`이 선언된 메서드가 호출될 때 AOP가 적용됩니다.
     */
    @Pointcut("@annotation(com.codesoom.assignment.common.authorization.IdVerification)")
    private void idVerificationMethod() {
    }

    /**
     * Access Token을 통해 전달되는 id와 @PathVariable을 통해 전달되는 id를 서로 비교합니다.
     *
     * @param joinPoint 메서드 관련 정보
     * @throws AccessDeniedException id가 존재하지 않거나 서로 다를 때 던집니다.
     */
    @Before("idVerificationMethod()")
    private void validateId(final JoinPoint joinPoint) throws AccessDeniedException {
        Long requestId = getIdByPathVariable(joinPoint);
        Long tokenId = getIdByUserAuthentication(joinPoint);

        if (!requestId.equals(tokenId)) {
            throw new AccessDeniedException("");
        }
    }


    // TODO: Controller 매개변수에 Long 타입이 두개 이상일 경우를 고려해야함
    //       @PathVariable 값을 가져오는 방법으로 수정하기
    private static Long getIdByPathVariable(final JoinPoint joinPoint) throws AccessDeniedException {
        return Arrays.stream(joinPoint.getArgs())
                .filter(Long.class::isInstance)
                .map(Long.class::cast)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException(""));
    }

    private static Long getIdByUserAuthentication(final JoinPoint joinPoint) throws AccessDeniedException {
        UserAuthentication userAuthentication = Arrays.stream(joinPoint.getArgs())
                .filter(UserAuthentication.class::isInstance)
                .map(UserAuthentication.class::cast)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException(""));

        return userAuthentication.getUserId();
    }
}
