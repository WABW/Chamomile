package io.wabw.controller;

import io.wabw.repository.SendMailBySSL;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by void on 2017/5/25.
 */
@Controller
public class SendMailController {

    @RequestMapping(value = "/mail/send/message", method = RequestMethod.GET)
    public String send(Model model) {


        return "/mail/send/page";
    }

    @RequestMapping(value = "/mail/send/message", method = RequestMethod.POST)
        public String SSend(Model model, String emailaddress1,String subject1,String content1) {
        SendMailBySSL.SSS(emailaddress1,subject1,content1);


        return "/mail/send/page";
    }

//    @RequestMapping(value = "/mail/message/action/back", method = RequestMethod.GET)
//    public String back() throws UnsupportedEncodingException {
//        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
//    }



}

