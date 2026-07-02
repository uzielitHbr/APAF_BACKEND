package app.apaf.backend.core.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


/*
Web security configuration class.
This class handles all HTTP-level security configurations for the API.

@Uziel Abraham
@Version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

   private final JwtAuthenticationFilter jwtAuthenticationFilter;
   private final AuthenticationProvider authenticationProvider;

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
              .authorizeHttpRequests(
                      command ->
                              command
                                      .requestMatchers("/api/v1/auth/login").permitAll()
                                      .requestMatchers("/api/v1/auth/recover-password").permitAll()
                                      .requestMatchers("/api/v1/auth/set-password").permitAll()
                                      .requestMatchers("/api/v1/auth/reset-password").permitAll()

                                      .requestMatchers(
                                              "/v3/api-docs/**",
                                              "/swagger-ui/**",
                                              "/swagger-ui.html"
                                      ).permitAll()
                              .anyRequest().authenticated()
                      )
              .sessionManagement(
                      session->
                              session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              )
              .csrf(csrf -> csrf.disable())
              .exceptionHandling(
              exception ->
                      exception
                      .authenticationEntryPoint((request, response, authException) -> {
                         System.out.println("Error " + authException.getMessage());
                         authException.printStackTrace();
                         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                      }) )
              .headers(headers->
                      headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              .authenticationProvider(authenticationProvider)
              .cors(cors ->
                      cors.configurationSource(corsConfigurationSource()));


      return http.build();
   }

   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(List.of(
              "http://localhost:8080",
              "https://unpoetically-interramal-loren.ngrok-free.dev"

      ));
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type","ngrok-skip-browser-warning"));
      configuration.setAllowCredentials(true);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
   }


}
