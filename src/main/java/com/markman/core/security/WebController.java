package com.markman.core.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class WebController {

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/")
    String home(@AuthenticationPrincipal MarkManPrincipal principal, Model model) {
        model.addAttribute("username", principal.getUsername());
        return "home";
    }
}
