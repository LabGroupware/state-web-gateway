package org.cresplanex.api.state.webgateway.config;

import jakarta.servlet.http.HttpServletRequest;
import org.cresplanex.api.state.webgateway.auth.CustomAccessDeniedHandler;
import org.cresplanex.api.state.webgateway.auth.CustomAuthenticationEntryPoint;
import org.cresplanex.api.state.webgateway.auth.RoleConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.front.origins}")
    private String frontOrigins;

    @Value("${app.require-https}")
    private boolean requireHttps;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new RoleConverter());
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();

                        List<String> origins = List.of(frontOrigins.split(","));

                        config.setAllowedOrigins(origins); // 許可するオリジン
                        config.setAllowedMethods(Collections.singletonList("*")); // 全てのHTTPメソッドを許可
                        config.setAllowCredentials(true); // クレデンシャル（Cookieや認証情報）を許可
                        config.setAllowedHeaders(Collections.singletonList("*")); // 全てのヘッダーを許可
                        config.setExposedHeaders(List.of("Authorization")); // レスポンスヘッダーに含めるヘッダー
                        config.setMaxAge(3600L); // プリフライトリクエストのキャッシュ時間
                        return config;
                    }
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .requiresChannel(
                        rcc -> {
                            if (requireHttps) {
                                rcc.anyRequest().requiresSecure();
                            }
                        }
                )
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(rsc ->
                        rsc.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .exceptionHandling(
                        ehc -> ehc
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                );
        return http.build();
    }
}
