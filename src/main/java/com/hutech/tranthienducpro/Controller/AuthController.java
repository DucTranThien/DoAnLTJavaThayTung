package com.hutech.tranthienducpro.Controller;


import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.service.MailServices;
import com.hutech.tranthienducpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private MailServices mailServices;

    @GetMapping("/me")
    public String GetCurrentUser(@AuthenticationPrincipal User userDetail, Model model){
        model.addAttribute("user", userDetail);
        return "Layout/Auth/currentUser";
    }
    @GetMapping("/change_password")
    public String  ChangePassword(@AuthenticationPrincipal User userDetail, Model model){
        model.addAttribute("user", userDetail);
        return "Layout/Auth/changepassword";
    }
    @PostMapping("change_password")
    public String SavePassword(@AuthenticationPrincipal User user,
                               @RequestParam("oldpassword") String oldPassword,
                               @RequestParam("newpassword") String newpassword){
        if(userService.checkOldPassword(user, oldPassword)){
            userService.UpdatePassword(user, newpassword);
            return "redirect:/change_password?done";
        }else{
            return "redirect:/change_password?error";
        }
    }
    @GetMapping("/forgotpassword")
    public String ForgotPassword(){
        return "Layout/Auth/forgotpassword";
    }
    @PostMapping("/forgotpassword")
    public String ForgotPassword(@RequestParam("email") String email){
        User user = userService.getUserByEmail(email);
        if(user != null){
            String token = userService.GenTokenResetPassword(user);
            String url = "http://localhost:8080/resetpassword?token="+token;
            mailServices.SendMail(user.getEmail(),url);
        }
        return "redirect:/login";
    }
    @GetMapping("/resetpassword")
    public String ResetPassword(@RequestParam("token") String token,Model model){
        User user = userService.getUserByToken(token);
        if(user != null){
            model.addAttribute("user", user);
        }
        return "Layout/Auth/reset_password";
    }
    @PostMapping("/resetpassword")
    public String ResetPassword_Save(@RequestParam("username") String username,
                                     @RequestParam("password") String password){
        User user = userService.getUserByUsername(username);
        userService.UpdatePassword(user,password);
        userService.ResetDateForgotPassword(user);
        return "redirect:/login";
    }
}
