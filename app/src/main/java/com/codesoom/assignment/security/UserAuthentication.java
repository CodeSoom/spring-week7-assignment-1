package com.codesoom.assignment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

//public class UserAuthentication implements Authentication { 너무 많은 것을 구현해 주어야 한다. 아래를 사용하자.
public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
        //return super.isAuthenticated();
    }

    private static List<GrantedAuthority> authorities() {
        // Todo userId에 따라서 다른 권한 부여. => ex. Admin, user
        return null;
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    /*
    public UserAuthentication(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }
    */
}
