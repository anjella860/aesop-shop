package com.aesop.shop.config;

import com.aesop.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;

    // 인증/인가 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/signup", "/login",
                                "/css/**", "/js/**", "/images/**",
                                "/api/products/**", "/api/categories/**",
                                "/api/notice/**", "/api/reviews/*/count"
                        ).permitAll()
                        .requestMatchers(
                                "/mypage/**", "/api/orders/**",
                                "/api/cart/**", "/api/reviews/**",
                                "/api/qna/**"
                        ).hasRole("USER")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form.loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .successHandler((req, res, auth) -> {
                                    res.setStatus(200);
                                })
                                .failureHandler((req, res, e) -> {
                                    res.setStatus(401);
                                })
                )
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                );

        return http.build();
    }

    // 사용자 인증 처리
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var member = memberService.findByEmail(username);
            return User.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .roles(member.getRole().name())
                    .build();
        };
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}