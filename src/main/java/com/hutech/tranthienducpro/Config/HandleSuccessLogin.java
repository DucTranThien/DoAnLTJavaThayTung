package com.hutech.tranthienducpro.Config;

import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class HandleSuccessLogin extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        if(user.getLockExpired()!=null){
            if(user.getLockExpired().getTime() < System.currentTimeMillis()){
                userService.ResetLoginFail(user);
            }
        }else{
            userService.ResetLoginFail(user);
        }

        boolean isAdmin = authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ADMIN")
        );

        if(isAdmin){
            setDefaultTargetUrl("/admin/users");
        }else{
            setDefaultTargetUrl("/products");
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
