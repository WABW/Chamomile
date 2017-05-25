package io.wabw.controller;

import io.wabw.helper.Decoder;
import io.wabw.misc.util.MessageUtil;
import io.wabw.repository.MailSession;
import org.simplejavamail.email.Email;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by MainasuK on 2017-5-5.
 */
@Controller
public class MessageController {

    Message message = null;
    MailSession session = null;
    Folder openedFolder = null;

    @RequestMapping(value = "/mail/message/{messageID}", method = RequestMethod.GET)
    public String redirect(@PathVariable String messageID, Model model) throws MessagingException, IOException {
        Message newMessage = (Message) model.asMap().get("_" + messageID);

        if (null != newMessage) {
            message = newMessage;
            session = (MailSession) model.asMap().get("_session");
            openedFolder = (Folder) model.asMap().get("_openedFolder");
        }

        assert(null != session);
        assert(null != openedFolder);

        if (null == message) {
            return "redirect:/mail/INBOX";
        }

        model.addAttribute("folders", session.getStore().getDefaultFolder().list());
        model.addAttribute("openedFolder", openedFolder);
        model.addAttribute("message", message);
        model.addAttribute("decoder", new Decoder());
        model.addAttribute("content", MessageUtil.getText(message));

        System.out.println(message.getFrom()[0].toString());

        return "/mail/message/page";
    }

    @RequestMapping(value = "/mail/message/action/back", method = RequestMethod.GET)
    public String back() throws UnsupportedEncodingException {
        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

}
