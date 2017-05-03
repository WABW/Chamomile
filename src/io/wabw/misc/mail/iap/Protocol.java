/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package io.wabw.misc.mail.iap;

import java.util.Vector;
import java.util.Properties;
import java.io.*;
import java.net.*;

/**
 * General protocol handling code for IMAP-like protocols. <p>
 *
 * The Protocol object is multithread safe.
 *
 * @author  John Mani
 * @author  Max Spivak
 * @author  Bill Shannon
 */

public class Protocol {
    protected String host;
    private Socket socket;
    // in case we turn on TLS, we'll need these later
    protected boolean debug;
    protected boolean quote;
    protected PrintStream out;
    protected Properties props;
    protected String prefix;

    private boolean connected = false;		// did constructor succeed?
    private io.wabw.misc.mail.util.TraceInputStream traceInput;	// the Tracer
    private volatile io.wabw.misc.mail.iap.ResponseInputStream input;

    private io.wabw.misc.mail.util.TraceOutputStream traceOutput;	// the Tracer
    private volatile DataOutputStream output;

    private int tagCounter = 0;

    private String localHostName;

    /*
     * handlers is a Vector, initialized here,
     * because we depend on it always existing and depend
     * on the synchronization that Vector provides.
     */
    private final Vector handlers = new Vector(); // response handlers

    private volatile long timestamp;

    private static final byte[] CRLF = { (byte)'\r', (byte)'\n'};
 
    /**
     * Constructor. <p>
     * 
     * Opens a connection to the given host at given port.
     *
     * @param host	host to connect to
     * @param port	portnumber to connect to
     * @param debug     debug mode
     * @param out	debug output stream
     * @param props     Properties object used by this protocol
     * @param prefix 	Prefix to prepend to property keys
     */
    public Protocol(String host, int port, boolean debug,
		    PrintStream out, Properties props, String prefix,
		    boolean isSSL) throws IOException, io.wabw.misc.mail.iap.ProtocolException {
	try {
	    this.host = host;
	    this.debug = debug;
	    this.out = out;
	    this.props = props;
	    this.prefix = prefix;

	    socket = io.wabw.misc.mail.util.SocketFetcher.getSocket(host, port, props, prefix, isSSL);
	    quote = io.wabw.misc.mail.util.PropUtil.getBooleanProperty(props,
					"mail.debug.quote", false);

	    initStreams(out);

	    // Read server greeting
	    processGreeting(readResponse());

	    timestamp = System.currentTimeMillis();
 
	    connected = true;	// must be last statement in constructor
	} finally {
	    /*
	     * If we get here because an exception was thrown, we need
	     * to disconnect to avoid leaving a connected socket that
	     * no one will be able to use because this object was never
	     * completely constructed.
	     */
	    if (!connected)
		disconnect();
	}
    }

    private void initStreams(PrintStream out) throws IOException {
	traceInput = new io.wabw.misc.mail.util.TraceInputStream(socket.getInputStream(), out);
	traceInput.setTrace(debug);
	traceInput.setQuote(quote);
	input = new io.wabw.misc.mail.iap.ResponseInputStream(traceInput);

	traceOutput = new io.wabw.misc.mail.util.TraceOutputStream(socket.getOutputStream(), out);
	traceOutput.setTrace(debug);
	traceOutput.setQuote(quote);
	output = new DataOutputStream(new BufferedOutputStream(traceOutput));
    }

    /**
     * Constructor for debugging.
     */
    public Protocol(InputStream in, OutputStream out, boolean debug)
				throws IOException {
	this.host = "localhost";
	this.debug = debug;
	this.quote = false;
	this.out = System.out;

	// XXX - inlined initStreams, won't allow later startTLS
	traceInput = new io.wabw.misc.mail.util.TraceInputStream(in, System.out);
	traceInput.setTrace(debug);
	traceInput.setQuote(quote);
	input = new io.wabw.misc.mail.iap.ResponseInputStream(traceInput);

	traceOutput = new io.wabw.misc.mail.util.TraceOutputStream(out, System.out);
	traceOutput.setTrace(debug);
	traceOutput.setQuote(quote);
	output = new DataOutputStream(new BufferedOutputStream(traceOutput));

        timestamp = System.currentTimeMillis();
    }

    /**
     * Returns the timestamp.
     */
 
    public long getTimestamp() {
        return timestamp;
    }
 
    /**
     * Adds a response handler.
     */
    public void addResponseHandler(ResponseHandler h) {
	handlers.addElement(h);
    }

    /**
     * Removed the specified response handler.
     */
    public void removeResponseHandler(ResponseHandler h) {
	handlers.removeElement(h);
    }

    /**
     * Notify response handlers
     */
    public void notifyResponseHandlers(io.wabw.misc.mail.iap.Response[] responses) {
	if (handlers.size() == 0)
	    return;
	
	for (int i = 0; i < responses.length; i++) { // go thru responses
	    io.wabw.misc.mail.iap.Response r = responses[i];

	    // skip responses that have already been handled
	    if (r == null)
		continue;

	    // Need to copy handlers list because handlers can be removed
	    // when handling a response.
	    Object[] h = handlers.toArray();

	    // dispatch 'em
	    for (int j = 0; j < h.length; j++) {
		if (h[j] != null)
		    ((ResponseHandler)h[j]).handleResponse(r);
	    }
	}
    }

    protected void processGreeting(io.wabw.misc.mail.iap.Response r) throws io.wabw.misc.mail.iap.ProtocolException {
	if (r.isBYE())
	    throw new io.wabw.misc.mail.iap.ConnectionException(this, r);
    }

    /**
     * Return the Protocol's InputStream.
     */
    protected io.wabw.misc.mail.iap.ResponseInputStream getInputStream() {
	return input;
    }

    /**
     * Return the Protocol's OutputStream
     */
    protected OutputStream getOutputStream() {
	return output;
    }

    /**
     * Returns whether this Protocol supports non-synchronizing literals
     * Default is false. Subclasses should override this if required
     */
    protected synchronized boolean supportsNonSyncLiterals() {
	return false;
    }

    public io.wabw.misc.mail.iap.Response readResponse()
		throws IOException, io.wabw.misc.mail.iap.ProtocolException {
	return new io.wabw.misc.mail.iap.Response(this);
    }

    /**
     * Return a buffer to be used to read a response.
     * The default implementation returns null, which causes
     * a new buffer to be allocated for every response.
     *
     * @since	JavaMail 1.4.1
     */
    protected io.wabw.misc.mail.iap.ByteArray getResponseBuffer() {
	return null;
    }

    public String writeCommand(String command, io.wabw.misc.mail.iap.Argument args)
		throws IOException, io.wabw.misc.mail.iap.ProtocolException {

    	if (2 == tagCounter) {
			output.writeBytes("C2 ID (\"name\" \"com.tencent.foxmail\" \"version\" \"7.2.7.174\" \"os\" \"windows\" \"os-version\" \"6.2\" \"vendor\" \"tencent limited\" \"contact\" \"foxmail@foxmail.com\")");
			tagCounter += 1;

			if (args != null) {
				output.write(' ');
				args.write(this);
			}

			output.write(CRLF);
			output.flush();
		}

	// assert Thread.holdsLock(this);
	// can't assert because it's called from constructor
	String tag = "C" + Integer.toString(tagCounter++, 10); // unique tag

	output.writeBytes(tag + " " + command);
    
	if (args != null) {
	    output.write(' ');
	    args.write(this);
	}

	output.write(CRLF);
	output.flush();
	return tag;
    }

    /**
     * Send a command to the server. Collect all responses until either
     * the corresponding command completion response or a BYE response 
     * (indicating server failure).  Return all the collected responses.
     *
     * @param	command	the command
     * @param	args	the arguments
     * @return		array of Response objects returned by the server
     */
    public synchronized io.wabw.misc.mail.iap.Response[] command(String command, io.wabw.misc.mail.iap.Argument args) {
	commandStart(command);
	Vector v = new Vector();
	boolean done = false;
	String tag = null;
	io.wabw.misc.mail.iap.Response r = null;

	// write the command
	try {
	    tag = writeCommand(command, args);
	} catch (io.wabw.misc.mail.iap.LiteralException lex) {
	    v.addElement(lex.getResponse());
	    done = true;
	} catch (Exception ex) {
	    // Convert this into a BYE response
	    v.addElement(io.wabw.misc.mail.iap.Response.byeResponse(ex));
	    done = true;
	}

	io.wabw.misc.mail.iap.Response byeResp = null;
	while (!done) {
	    try {
		r = readResponse();
	    } catch (IOException ioex) {
		if (byeResp != null)	// connection closed after BYE was sent
		    break;
		// convert this into a BYE response
		r = io.wabw.misc.mail.iap.Response.byeResponse(ioex);
	    } catch (io.wabw.misc.mail.iap.ProtocolException pex) {
		continue; // skip this response
	    }

	    if (r.isBYE()) {
		byeResp = r;
		continue;
	    }

	    v.addElement(r);

	    // If this is a matching command completion response, we are done
	    if (r.isTagged() && r.getTag().equals(tag))
		done = true;
	}

	if (byeResp != null)
		v.addElement(byeResp);	// must be last
	io.wabw.misc.mail.iap.Response[] responses = new io.wabw.misc.mail.iap.Response[v.size()];
	v.copyInto(responses);
        timestamp = System.currentTimeMillis();
	commandEnd();
	return responses;
    }

    /**
     * Convenience routine to handle OK, NO, BAD and BYE responses.
     */
    public void handleResult(io.wabw.misc.mail.iap.Response response) throws io.wabw.misc.mail.iap.ProtocolException {
	if (response.isOK())
	    return;
	else if (response.isNO())
	    throw new CommandFailedException(response);
	else if (response.isBAD())
	    throw new io.wabw.misc.mail.iap.BadCommandException(response);
	else if (response.isBYE()) {
	    disconnect();
	    throw new io.wabw.misc.mail.iap.ConnectionException(this, response);
	}
    }

    /**
     * Convenience routine to handle simple IAP commands
     * that do not have responses specific to that command.
     */
    public void simpleCommand(String cmd, io.wabw.misc.mail.iap.Argument args)
			throws io.wabw.misc.mail.iap.ProtocolException {
	// Issue command
	io.wabw.misc.mail.iap.Response[] r = command(cmd, args);

	// dispatch untagged responses
	notifyResponseHandlers(r);

	// Handle result of this command
	handleResult(r[r.length-1]);
    }

    /**
     * Start TLS on the current connection.
     * <code>cmd</code> is the command to issue to start TLS negotiation.
     * If the command succeeds, we begin TLS negotiation.
     */
    public synchronized void startTLS(String cmd)
				throws IOException, io.wabw.misc.mail.iap.ProtocolException {
	simpleCommand(cmd, null);
	socket = io.wabw.misc.mail.util.SocketFetcher.startTLS(socket, host, props, prefix);
	initStreams(out);
    }

    /**
     * Disconnect.
     */
    protected synchronized void disconnect() {
	if (socket != null) {
	    try {
		socket.close();
	    } catch (IOException e) {
		// ignore it
	    }
	    socket = null;
	}
    }

    /**
     * Get the name of the local host.
     * The property <prefix>.localhost overrides <prefix>.localaddress,
     * which overrides what InetAddress would tell us.
     */
    protected synchronized String getLocalHost() {
	// get our hostname and cache it for future use
	if (localHostName == null || localHostName.length() <= 0)
	    localHostName =
		    props.getProperty(prefix + ".localhost");
	if (localHostName == null || localHostName.length() <= 0)
	    localHostName =
		    props.getProperty(prefix + ".localaddress");
	try {
	    if (localHostName == null || localHostName.length() <= 0) {
		InetAddress localHost = InetAddress.getLocalHost();
		localHostName = localHost.getCanonicalHostName();
		// if we can't get our name, use local address literal
		if (localHostName == null)
		    // XXX - not correct for IPv6
		    localHostName = "[" + localHost.getHostAddress() + "]";
	    }
	} catch (UnknownHostException uhex) {
	}

	// last chance, try to get our address from our socket
	if (localHostName == null || localHostName.length() <= 0) {
	    if (socket != null && socket.isBound()) {
		InetAddress localHost = socket.getLocalAddress();
		localHostName = localHost.getCanonicalHostName();
		// if we can't get our name, use local address literal
		if (localHostName == null)
		    // XXX - not correct for IPv6
		    localHostName = "[" + localHost.getHostAddress() + "]";
	    }
	}
	return localHostName;
    }

    /**
     * Temporarily turn off protocol tracing, e.g., to prevent
     * tracing the authentication sequence, including the password.
     */
    protected void suspendTracing() {
	if (debug) {
	    traceInput.setTrace(false);
	    traceOutput.setTrace(false);
	}
    }

    /**
     * Resume protocol tracing, if it was enabled to begin with.
     */
    protected void resumeTracing() {
	if (debug) {
	    traceInput.setTrace(true);
	    traceOutput.setTrace(true);
	}
    }

    /**
     * Finalizer.
     */
    protected void finalize() throws Throwable {
	super.finalize();
	disconnect();
    }

    /*
     * Probe points for GlassFish monitoring.
     */
    private void commandStart(String command) { }
    private void commandEnd() { }
}
