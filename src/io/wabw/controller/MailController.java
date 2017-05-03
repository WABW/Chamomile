package io.wabw.controller;

import io.wabw.repository.MailSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by MainasuK on 2017-5-3.
 */
@Controller
public class MailController {

    private Folder openedFolder = null;

    @RequestMapping(value = "/mail/{folderName}", method = RequestMethod.GET)
    public String mail(@PathVariable String folderName, Model model, HttpServletRequest request) throws MessagingException {
        final String kFlashMailSession = "mail_session";

        // close opened folder first
        if (null != openedFolder) {
            openedFolder.close(true);
        }
        assert(!openedFolder.isOpen());

        // Check mail store
        if (!model.containsAttribute(kFlashMailSession) && null == request.getSession().getAttribute("mail")) {
            return "redirect:/login";
        }

        // FIXME: add exception handler
        MailSession session = (MailSession) model.asMap().getOrDefault(kFlashMailSession, request.getSession().getAttribute("mail"));
        Store store = session.getStore();
        Folder folder = store.getFolder(folderName);
        if (!folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }

        // FIME: add mailSession to session
        request.getSession().setAttribute("mail", session);
        model.addAttribute("folders", store.getDefaultFolder().list());
        model.addAttribute("openedFolder", folder);

//        folder.close(true);
//        store.close();

        openedFolder = folder;
        assert(folder.isOpen());
        assert(null != openedFolder);

        return "mail/page";
    }

    @RequestMapping(value = "/mail/action/read", method = RequestMethod.POST)
    public String mail(@RequestParam("mailNumbers[]") int[] mailNumbers) throws UnsupportedEncodingException {

        for (int i : mailNumbers) {
            System.out.println(i);
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }
}
