package com.codesoom.assignment.authetication;

import com.codesoom.assignment.domain.UserType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;
    private final UserType userType;

    public UserAuthentication(Long userId, UserType userType) {
        super(authorities(userType));
        this.userId = userId;
        this.userType = userType;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Used to indicate to AbstractSecurityInterceptor whether it should present the authentication token to the AuthenticationManager.
     *
     * @return
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    /**
     * userId 같이 인증할 떄 들어오는 아이디 같은걸 반환한다.
     * 대부분 userDetail을 사용한다 pricipal을 위해서
     *
     * @return
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public String toString() {
        return "UserAuthentication{" +
                "userId=" + userId +
                '}';
    }

    private static List<? extends GrantedAuthority> authorities(UserType userType) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        //TODO userID에 따라서 유저 권한 나눠주기

        authorities.add(new SimpleGrantedAuthority(userType.toString()));
        return authorities;
    }

}
