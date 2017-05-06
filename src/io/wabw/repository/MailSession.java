package io.wabw.repository;

import javax.mail.*;
import java.util.Properties;

/**
 * Created by MainasuK on 2017-5-2.
 */

public class MailSession {

    private Properties properties;
    private Authenticator authenticator;
    private Session session;

    public MailSession(Properties properties, Authenticator authenticator) {
        this.properties = properties;
        this.authenticator = authenticator;
        this.session = Session.getDefaultInstance(properties, authenticator);

//        session.setDebug(true);
    }

    public Transport getTransport() throws MessagingException {
        Transport transport = session.getTransport("smtp");
        transport.connect();

        return transport;
    }

    public Store getStore() throws MessagingException {
        Store store = null;

        if ("imap.126.com".equals(session.getProperty("mail.imap.host"))) {
            store = session.getStore(new Provider(Provider.Type.STORE, "imap", "io.wabw.misc.mail.imap.IMAPStore", "Sun Microsystems, Inc.", "1.4.7"));
        } else {
            store = session.getStore();
        }

        store.connect();

        return store;
    }

}
