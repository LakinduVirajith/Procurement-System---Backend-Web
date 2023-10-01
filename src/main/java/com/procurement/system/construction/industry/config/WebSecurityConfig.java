package com.procurement.system.construction.industry.config;

import com.procurement.system.construction.industry.config.jwt.JwtAuthenticationFilter;
import com.procurement.system.construction.industry.enums.Permission;
import com.procurement.system.construction.industry.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] WHITELIST = {
            "/",
            "/super/admin",
            "/api/v1/user/login",
            "/api/v1/user/refresh-token",

            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private final JwtAuthenticationFilter JwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(WHITELIST).permitAll()
                        .requestMatchers("/api/v1/site-manager/**").hasAnyRole(UserRole.SITE_MANAGER.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/site-manager/**").hasAnyAuthority(Permission.SITE_MANAGER_READ.name(), Permission.ADMIN_READ.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/site-manager/**").hasAnyAuthority(Permission.SITE_MANAGER_CREATE.name(), Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/site-manager/**").hasAnyAuthority(Permission.SITE_MANAGER_UPDATE.name(), Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/site-manager/**").hasAnyAuthority(Permission.SITE_MANAGER_DELETE.name(), Permission.ADMIN_DELETE.name())

                        .requestMatchers("/api/v1/procurement-manager/**").hasAnyRole(UserRole.PROCUREMENT_MANAGER.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/procurement-manager/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_READ.name(), Permission.ADMIN_READ.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/procurement-manager/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_CREATE.name(), Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/procurement-manager/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_UPDATE.name(), Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/procurement-manager/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_DELETE.name(), Permission.ADMIN_DELETE.name())
                        
                        .requestMatchers("/api/v1/supplier/**").hasAnyRole(UserRole.SUPPLIER.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/supplier/**").hasAnyAuthority(Permission.SUPPLIER_READ.name(), Permission.ADMIN_READ.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/supplier/**").hasAnyAuthority(Permission.SUPPLIER_CREATE.name(), Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/supplier/**").hasAnyAuthority(Permission.SUPPLIER_UPDATE.name(), Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/supplier/**").hasAnyAuthority(Permission.SUPPLIER_DELETE.name(), Permission.ADMIN_DELETE.name())

                        .requestMatchers("/api/v1/all-users/**").hasAnyRole(UserRole.PROCUREMENT_MANAGER.name(), UserRole.SITE_MANAGER.name(), UserRole.SUPPLIER.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/supplier/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_READ.name(), Permission.SITE_MANAGER_READ.name(), Permission.SUPPLIER_READ.name(), Permission.ADMIN_READ.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/supplier/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_CREATE.name(), Permission.SITE_MANAGER_CREATE.name(), Permission.SUPPLIER_CREATE.name(), Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/supplier/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_UPDATE.name(), Permission.SITE_MANAGER_UPDATE.name(), Permission.SUPPLIER_UPDATE.name(), Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/supplier/**").hasAnyAuthority(Permission.PROCUREMENT_MANAGER_DELETE.name(), Permission.SITE_MANAGER_DELETE.name(), Permission.SUPPLIER_DELETE.name(), Permission.ADMIN_DELETE.name())

                        .requestMatchers("/api/v1/super-admin/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/super-admin/**").hasAuthority(Permission.ADMIN_READ.name())
                        .requestMatchers(HttpMethod.POST, "/api/v1/super-admin/**").hasAuthority(Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/super-admin/**").hasAuthority(Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/super-admin/**").hasAuthority( Permission.ADMIN_DELETE.name())
                        .anyRequest().authenticated())

                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(JwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
