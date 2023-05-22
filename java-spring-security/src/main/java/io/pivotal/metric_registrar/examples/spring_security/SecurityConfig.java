package io.pivotal.metric_registrar.examples.spring_security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .requestMatchers(examplesRequestMatcher()).permitAll()
            .and().authorizeRequests()
            .requestMatchers((request) -> request.getServletPath().equals("/")).permitAll()
            .and().authorizeRequests()
            .requestMatchers(endpointRequestMatcher()).permitAll()
            .and().authorizeRequests()
            .anyRequest().denyAll();
        return http.build();
    }

    private RequestMatcher endpointRequestMatcher() {
        return EndpointRequest.to("metrics", "prometheus");
    }

    private RequestMatcher examplesRequestMatcher() {
        return (request) -> request.getServletPath().equals("/high_latency") ||
                request.getServletPath().equals("/custom_metric") ||
                request.getServletPath().equals("/simple");
    }

}
