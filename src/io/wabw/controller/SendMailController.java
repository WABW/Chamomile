package io.wabw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by void on 2017/5/25.
 */
@Controller
public class SendMailController {

    @RequestMapping(value = "/mail/send/message", method = RequestMethod.GET)
    public String send(Model model) {


        return "/mail/send/page";
    }

//    @RequestMapping(value = "/mail/message/action/back", method = RequestMethod.GET)
//    public String back() throws UnsupportedEncodingException {
//        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
//    }



}

