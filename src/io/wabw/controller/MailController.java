package io.wabw.controller;

import io.wabw.repository.MailSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by MainasuK on 2017-5-3.
 */
@Controller
public class MailController {

    private Folder openedFolder = null;
    private MailSession mailSession = null;

    @RequestMapping(value = "/mail/action/message/{messageNumber}", method = RequestMethod.GET)
    public String message(@PathVariable int messageNumber, RedirectAttributes model) throws MessagingException {
        Message message = openedFolder.getMessage(messageNumber);
        if (null == message) {
            return "redirect:/mail/" + openedFolder.getFullName();
        }

        assert(null != mailSession);
        assert(null != openedFolder);

        model.addFlashAttribute("_" + message.hashCode(), message);
        model.addFlashAttribute("_session", mailSession);
        model.addFlashAttribute("_openedFolder", openedFolder);

        return "redirect:/mail/message/" + message.hashCode();
    }

    @RequestMapping(value = "/mail/{folderName}", method = RequestMethod.GET)
    public String mail(@PathVariable String folderName, Model model, HttpServletRequest request) throws MessagingException {
        final String kFlashMailSession = "mail_session";

        // close opened folder first
        if (null != openedFolder && openedFolder.isOpen()) {
            openedFolder.close(true);
        }
        assert(!openedFolder.isOpen());

        // Check mail store
        if (!model.containsAttribute(kFlashMailSession) && null == request.getSession().getAttribute("mail")) {
            return "redirect:/login";
        }

        // FIXME: add exception handler
        MailSession session = (MailSession) model.asMap().getOrDefault(kFlashMailSession, request.getSession().getAttribute("mail"));
        this.mailSession = session;
        Store store = session.getStore();
        Folder folder = store.getFolder(folderName);
        if (!folder.isOpen()) {
            folder.open(Folder.READ_WRITE);
        }

        // FIME: add mailSession to session
        request.getSession().setAttribute("mail", session);
        model.addAttribute("folders", store.getDefaultFolder().list());
        model.addAttribute("openedFolder", folder);

        Message[] messages = folder.getMessages();
        List<Message> messageList = Arrays.asList(messages);
        Collections.reverse(messageList);
        messages = (Message[]) messageList.toArray();
        model.addAttribute("messages", messages);

//        folder.close(true);
//        store.close();

        openedFolder = folder;
        assert(folder.isOpen());
        assert(null != openedFolder);

        model.addAttribute("Flag_SEEN", Flags.Flag.SEEN);
        model.addAttribute("Flag_FLAGGED", Flags.Flag.FLAGGED);

        return "mail/page";
    }

    @RequestMapping(value = "/mail/action/read", method = RequestMethod.POST)
    public String read(@RequestParam(value = "mailNumbers[]", required = false) int[] mailNumbers) throws UnsupportedEncodingException, MessagingException {

        if (null == mailNumbers) {
            return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
        }

        for (Message message : openedFolder.getMessages(mailNumbers)) {
            message.setFlag(Flags.Flag.SEEN, true);
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

    @RequestMapping(value = "/mail/action/unread", method = RequestMethod.POST)
    public String unread(@RequestParam(value = "mailNumbers[]", required = false) int[] mailNumbers) throws UnsupportedEncodingException, MessagingException {

        if (null == mailNumbers) {
            return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
        }

        for (Message message : openedFolder.getMessages(mailNumbers)) {
            message.setFlag(Flags.Flag.SEEN, false);
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

    public Flags.Flag SEEN() {
        return Flags.Flag.SEEN;
    }

    @RequestMapping(value = "/mail/action/flag", method = RequestMethod.POST)
    public String flag(@RequestParam(value = "mailNumbers[]", required = false) int[] mailNumbers) throws UnsupportedEncodingException, MessagingException {

        if (null == mailNumbers) {
            return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
        }

        for (Message message : openedFolder.getMessages(mailNumbers)) {
            message.setFlag(Flags.Flag.FLAGGED, true);
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

    @RequestMapping(value = "/mail/action/unflag", method = RequestMethod.POST)
    public String unflag(@RequestParam(value = "mailNumbers[]", required = false) int[] mailNumbers) throws UnsupportedEncodingException, MessagingException {

        if (null == mailNumbers) {
            return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
        }

        for (Message message : openedFolder.getMessages(mailNumbers)) {
            message.setFlag(Flags.Flag.FLAGGED, false);
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

    @RequestMapping(value = "/mail/action/trash", method = RequestMethod.POST)
    public String move(@RequestParam(value = "mailNumbers[]", required = false) int[] mailNumbers) throws UnsupportedEncodingException, MessagingException {
        if (null == mailNumbers) {
            return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
        }

        Message[] messages = openedFolder.getMessages(mailNumbers);
        Folder newFolder = mailSession.getStore().getFolder("已删除");
        if (0 != messages.length && null != newFolder) {
            openedFolder.copyMessages(messages, newFolder);
            openedFolder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
            openedFolder.expunge();
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }


    @RequestMapping(value = "/mail/action/move/{folderMoveTo}", method = RequestMethod.POST)
    public String move(@RequestParam(value = "mailNumbers[]", required = false) int[] mailNumbers, @PathVariable String folderMoveTo) throws UnsupportedEncodingException, MessagingException {
        if (null == mailNumbers) {
            return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
        }

        Message[] messages = openedFolder.getMessages(mailNumbers);
        Folder newFolder = mailSession.getStore().getFolder(folderMoveTo);
        if (0 != messages.length && null != newFolder) {
            openedFolder.copyMessages(messages, newFolder);
            openedFolder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
            openedFolder.expunge();
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

    @RequestMapping(value = "/mail/action/markAllRead", method = RequestMethod.POST)
    public String move() throws UnsupportedEncodingException, MessagingException {


        Message[] unreadMessages = openedFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        for (Message message : unreadMessages) {
            message.setFlag(Flags.Flag.SEEN, true);
        }

        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

    @RequestMapping(value = "/mail/action/refresh", method = RequestMethod.POST)
    public String refresh() throws UnsupportedEncodingException {
        return "redirect:/mail/" + URLEncoder.encode(openedFolder.getFullName(), "UTF-8");
    }

}
