package com.codesoom.assignment.dto;

import com.codesoom.assignment.suppliers.AuthenticationDataSupplier;
import lombok.Generated;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SessionRequestData implements AuthenticationDataSupplier {
    private String email;
    private String password;

    public SessionRequestData() {
    }

    public SessionRequestData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Generated
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

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
