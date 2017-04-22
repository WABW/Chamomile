package io.wabw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by MainasuK on 2017-4-22.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(RedirectAttributes model) {
        // This attribute is flash (only live over the redirect, then disappear)
        model.addFlashAttribute("flash_key", "username");

        return "redirect:/";
    }

}
