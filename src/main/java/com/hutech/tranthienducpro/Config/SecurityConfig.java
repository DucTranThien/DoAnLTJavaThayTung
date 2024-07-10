package com.hutech.tranthienducpro.Config;

import com.hutech.tranthienducpro.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration // Đánh dấu lớp này là một lớp cấu hình cho Spring Context.
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security.
@RequiredArgsConstructor // Lombok tự động tạo constructor có tham số cho tất cả các trường final.
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private HandleSuccessLogin handleSuccessLogin;

    @Autowired
    private HandleFailureLogin handleFailureLogin;

    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/", "/oauth/**",  "/register", "/error",
                            "/products", "/cart", "/cart/**","/uploads/**")
                .permitAll()
                .requestMatchers("/api/**","/api/search/products/")
                .permitAll() // API mở cho mọi người dùng.
                .requestMatchers("products/detail/**", "products/search","forgotpassword","resetpassword")
                .permitAll() // Cho phép truy cập không cần xác thực.
                .requestMatchers("/products/edit/**", "/products/add", "/products/delete")
                .hasAnyAuthority("ADMIN") // Chỉ cho phép ADMIN truy cập.
                .requestMatchers("/admin/**").hasAuthority("MASTER") // Bảo vệ các URL của admin
                .anyRequest().authenticated() // Bất kỳ yêu cầu nào khác cần xác thực.
            ).csrf(AbstractHttpConfigurer::disable)
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login") // Trang chuyển hướng sau khi đăng xuất.
                    .deleteCookies("JSESSIONID") // Xóa cookie.
                    .invalidateHttpSession(true) // Hủy phiên làm việc.
                    .clearAuthentication(true) // Xóa xác thực.
                    .permitAll()
            )
            .formLogin(formLogin -> formLogin
                    .loginPage("/login") // Trang đăng nhập.
                    .successHandler(handleSuccessLogin)
                    .failureHandler(handleFailureLogin)
                    .defaultSuccessUrl("/products") // Trang sau đăng nhập thành công.
                    .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                    .key("JSESSIONID")
                    .rememberMeCookieName("JSESSIONID")
                    .tokenValiditySeconds(24 * 60 * 60) // Thời gian nhớ đăng nhập.
                    .userDetailsService(userDetailsService())
            )
            .logout(logout->logout.
                    deleteCookies("JSESSIONID"))
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .accessDeniedPage("/403") // Trang báo lỗi khi truy cập không được phép.
            )
            .sessionManagement(sessionManagement -> sessionManagement
                    .maximumSessions(1) // Giới hạn số phiên đăng nhập.
                    .expiredUrl("/login") // Trang khi phiên hết hạn.
            )
            .httpBasic(httpBasic -> httpBasic
                    .realmName("hutech") // Tên miền cho xác thực cơ bản.
            )
            .build(); // Xây dựng và trả về chuỗi lọc bảo mật.
    }

    public UserService userDetailsService() {
        return userService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
