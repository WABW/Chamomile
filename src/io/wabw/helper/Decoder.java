package io.wabw.helper;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;

/**
 * Created by MainasuK on 2017-5-6.
 */
public class Decoder {

    public String decodeText(String text) throws UnsupportedEncodingException {
        return MimeUtility.decodeText(text);
    }

}
