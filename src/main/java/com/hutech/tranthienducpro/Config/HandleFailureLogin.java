package com.hutech.tranthienducpro.Config;

import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
@Configuration
public class HandleFailureLogin extends SimpleUrlAuthenticationFailureHandler{
    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        User user = userService.getUserByUsername(username);
        if (user != null) {
            userService.UpdateFailCount(user);
        }
        System.out.println("That bai");
        setDefaultFailureUrl("/login");
        super.onAuthenticationFailure(request, response, exception);
    }
}
