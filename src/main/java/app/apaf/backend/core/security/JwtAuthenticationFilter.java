package app.apaf.backend.core.security;


import app.apaf.backend.core.security.impl.UserDetailsImpl;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
Implement JwtAuthenticationFilter
Filter to intercept and validate Token Jwt in every single request
@Author Uziel Abraham
@Version 1.0
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
           JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository= userRepository;
    }


    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
            )
        throws ServletException , IOException
    {
        try {
            String token = jwtService.getJwtTokenFromHeader(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
                log.info("Authentication Token: {}", token);

            String idUser = jwtService.getIdUsuarioFromJwtToken(token);
            if (jwtService.validateJwtToken(token)) {

                Long id = Long.parseLong(idUser);
                User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found "));
                UserDetails userDetails = new UserDetailsImpl(user);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e){
            // If there's an error parsing the token, log it and continue
            log.error("JWT Token parsing failed: {}", e.getMessage());

        }

        filterChain.doFilter(request,response);
    }

}
