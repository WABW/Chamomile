package io.wabw.controller;

import io.wabw.repository.MailProperties;
import io.wabw.repository.MailSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Store;

/**
 * Created by MainasuK on 2017-4-22.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login/page";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String email, String password, RedirectAttributes model) throws MessagingException {

        MailSession session = new MailSession(MailProperties.imap126Mail(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        // This attribute is flash (only live over the redirect, then disappear)
        model.addFlashAttribute("mail_session", session);

        return "redirect:/mail/INBOX";
    }

}
