package app.apaf.backend.core.security.impl;


import app.apaf.backend.domain.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import app.apaf.backend.domain.users.User;

import java.util.Collection;
import java.util.List;

import static java.time.LocalDateTime.now;


/*
This function wraps the domain User entity into Spring Security's UserDetails,
it improves architectural efficiency by keeping the core entity decoupled from security frameworks.
@Uziel Abraham
@Version 1.0
 */
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;


    public User getUser() {
        return this.user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {

        if (user.isAccountLocked()) {
            return false;
        }
        if (user.getLockTime() == null) {
            return true;
        }
        return now().isAfter(user.getLockTime().plusMinutes(1));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVO.equals(user.getStatus());
    }
}
