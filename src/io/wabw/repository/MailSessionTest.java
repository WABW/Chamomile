package io.wabw.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by MainasuK on 2017-5-2.
 */
public class MailSessionTest {

    MailSession session = null;

    @Before
    public void setUp() throws Exception {
        Properties properties = MailProperties.imap126Mail();
        Authenticator authentication = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("email_test_2017@126.com", "et2me0");
            }
        };

        this.session = new MailSession(properties, authentication);
    }

    @After
    public void tearDown() throws Exception {
        session = null;
    }

    @Test
    public void testGetTransport() {
        try {
            Assert.notNull(session.getTransport(), "Get mail transport (SMTP)");
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            Assert.isNull(e, "Should not auth fail");
        } catch (MessagingException e) {
            e.printStackTrace();
            Assert.isNull(e, "Catch other mail exception");
        }
    }

    // Connect to IMAP is much faster than auth via SMTP!
    @Test
    public void connect() {
        try {
            Store store = session.getStore();
            Assert.notNull(store, "Get mail store (IMAP)");

            Folder[] folders = store.getDefaultFolder().list();
            for (Folder folder: folders) {
                System.out.println("-> " + folder.getName());
            }

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            System.out.println("");
            for (Message message: inbox.getMessages()) {
//                System.out.println(message.getContentType());
                System.out.println(message.getSubject());

                if (message.getContent() instanceof Multipart) {
                    System.out.println("MIME multipart");
                    message.writeTo(System.out);
                } else {
                    System.out.println(message.getContent());
                }
            }

            store.close();
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            Assert.isNull(e, "Should not auth fail");
        } catch (MessagingException e) {
            e.printStackTrace();
            Assert.isNull(e, "Catch other mail exception");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

