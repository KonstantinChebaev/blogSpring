package main.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    @RequestMapping(value = {
            "/init",
            "/image",
            "/comment",
            "/tag",
            "/auth/*",
            "/edit/*",
            "/calendar/*",
            "/my/*",
            "/login",
            "/login/*",
            "/moderator/*",
            "/moderation/*",
            "/post/*",
            "/posts/*",
            "/profile",
            "settings",
            "/stat",
            "/login/change-password",
            "/404"
    })
    public String frontend(Model model) {
        return "forward:/";
    }


}
