package io.wabw.repository;

import java.util.Properties;

/**
 * Created by MainasuK on 2017-5-3.
 */
public class MailProperties {

    public static Properties imap126Mail() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.starttls.enable", "true");
//        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.host", "imap.126.com");
        properties.put("mail.imap.port", "143");
        properties.put("mail.imap.partialfetch", "false");
        properties.put("mail.mime.charset", "GBK");

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host","smtp.126.com");
        properties.put("mail.smtp.port","25");

        return properties;
    }

}
