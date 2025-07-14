package com.example.identityservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/v1/api-docs/**"
    };

    private final String[] AUTH_ENDPOINTS = {
            "/user/register",
            "/auth/log-in",
            "/auth/introspect",
            "/auth/refresh",
            "/auth/logout"
    };
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {    //fixed
//        httpSecurity
//                .authorizeHttpRequests(request -> request
//                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
//                        .requestMatchers(HttpMethod.POST, AUTH_ENDPOINTS).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .csrf(AbstractHttpConfigurer::disable) // Có thể bật nếu frontend hỗ trợ CSRF token
//                .headers(headers -> headers
//                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
//                        .contentTypeOptions(withDefaults -> {})
//                        .httpStrictTransportSecurity(hsts ->
//                                hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
//                        .contentSecurityPolicy(csp ->
//                                csp.policyDirectives("default-src 'self'"))
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.decoder(jwtDecoder())
//                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
//                        .authenticationEntryPoint(customAuthenticationEntryPoint)
//                );
//        return httpSecurity.build();
//    }
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:8386")
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("Authorization", "Content-Type")
//                .allowCredentials(true);
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {    //get error
        httpSecurity
                .authorizeHttpRequests(request -> request
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, AUTH_ENDPOINTS).permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return httpSecurity.build();
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return customJwtDecoder;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
