package app.apaf.backend.core.security.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import app.apaf.backend.domain.users.User;

import java.util.Collection;
import java.util.List;


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
        return List.of();
        // return List.of(new SimpleGrantedAuthority(user.getPermisoOPerfil()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
