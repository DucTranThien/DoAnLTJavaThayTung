package com.hutech.tranthienducpro.Controller;

import com.hutech.tranthienducpro.model.User;
import com.hutech.tranthienducpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@Secured("MASTER")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/changeRole")
    public String changeUserRole(@RequestParam Long userId, @RequestParam String role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "admin/success";
    }
}
