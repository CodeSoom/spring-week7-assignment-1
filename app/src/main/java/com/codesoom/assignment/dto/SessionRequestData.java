package com.codesoom.assignment.dto;

import com.codesoom.assignment.suppliers.Identifier;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SessionRequestData implements Identifier {
    private String email;
    private String password;

    public SessionRequestData() {}

    public SessionRequestData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionRequestData)) {
            return false;
        }
        SessionRequestData that = (SessionRequestData) o;
        return Objects.equals(email, that.email)
                && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
