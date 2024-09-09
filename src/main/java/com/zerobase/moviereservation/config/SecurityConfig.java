package com.zerobase.moviereservation.config;

import com.zerobase.moviereservation.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter authenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.requestMatchers("/auth/**", "/reservations/**", "/seats/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/movies/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/schedules/**").hasRole("USER")
                .requestMatchers("/theaters/**").hasRole("OWNER")
                .requestMatchers("/movies/**").hasRole("OWNER")
                .requestMatchers("/schedules/**").hasRole("OWNER")
        )
        .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
