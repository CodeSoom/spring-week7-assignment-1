package security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;

    public UserAuthentication(Long userId) {
        super(authorities());
        this.userId = userId;
    }

    private static List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    /**
     * 사용자의 증명서를 리턴한다.
     *
     * @return 사용자의 증명서
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 인증 상태를 리턴한다.
     *
     * @return 인증 상태
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    /**
     * 사용자 id를 리턴한다.
     *
     * @return 사용자 id
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public String toString(){
        return "Authentication(" + userId + ")";
    }
}
