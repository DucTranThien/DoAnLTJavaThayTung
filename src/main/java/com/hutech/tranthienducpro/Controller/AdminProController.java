package com.hutech.tranthienducpro.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminProController {
    @GetMapping("AdminPro/AdminDashBoard")
    public String adminProDashBoard(Model model) {
        model.addAttribute("message", "XIN CHÀO các PHONEr của chúng tôi!");
        return "AdminPro/AdminDashBoard";
    }
}
