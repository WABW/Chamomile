package io.wabw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by CMNew on 2017/4/21.
 */
@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {

        // Check user signed or not
        if (!model.containsAttribute("flash_key")) {
            return "redirect: /login";
        }

        return "index";
    }

}
