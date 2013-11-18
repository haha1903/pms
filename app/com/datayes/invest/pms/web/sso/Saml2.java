package com.datayes.invest.pms.web.sso;

import com.datayes.paas.sso.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by changhai on 13-9-16.
 */
public class Saml2 {
    
    private static Logger LOGGER = LoggerFactory.getLogger(Saml2.class);
    
    private static final String SAML2_RESPONSE = "saml2p:Response";
    private static final String SAML_AUTH_REQUEST = "samlp:AuthnRequest";
    private static final String SAML2_LOGOUT_REQUEST = "saml2p:LogoutRequest";
    private static final String SAML2_LOGOUT_RESPONSE = "saml2p:LogoutResponse";
    private static final String SAML2_NAME_ID = "saml2:NameID";
    private static final String SINGLE_LOGOUT = "Single Logout";
    private static final String SAML2_ISSUER = "saml2:Issuer";
    private static final String SAML2_STATUS_CODE = "saml2p:StatusCode";
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static DocumentBuilder documentBuilder;
    
    private String scheme = "4.5";

    static {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("init saml2 failure", e);
        }
    }

    public String buildAuthRequest(String consumerUrl, String consumerIndex) throws IOException {
        Issuer issuer = new Issuer();
        issuer.setValue(consumerUrl);
        NameIDPolicy nameIdPolicy = new NameIDPolicy();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setIssueInstant(new Date());
        authRequest.setAssertionConsumerServiceURL(consumerUrl);
        authRequest.setIssuer(issuer);
        authRequest.setNameIDPolicy(nameIdPolicy);
        RequestedAuthContext requestedAuthContext = new RequestedAuthContext();
        authRequest.setRequestedAuthnContext(requestedAuthContext);
        authRequest.setID(Integer.toHexString(new Double(Math.random()).intValue()));
        if (consumerIndex != null)
            authRequest.setAttributeConsumingServiceIndex(Integer.parseInt(consumerIndex));
        return encode(getDocument(authRequest));
    }

    public String buildLogoutRequest(String name, String consumerUrl) throws IOException {
        LogoutRequest request = new LogoutRequest();
        Issuer issuer = new Issuer();
        issuer.setValue(consumerUrl);
        request.setIssuer(issuer);
        NameID nameID = new NameID();
        nameID.setValue(name);
        request.setNameID(nameID);
        request.setReason(SINGLE_LOGOUT);
        SessionIndex sessionIndex = new SessionIndex();
        sessionIndex.setSessionIndex(UUID.randomUUID().toString());
        request.setSessionIndex(sessionIndex);
        return encode(getDocument(request));
    }

    public Message unmarshall(String message) throws IOException {
        try {
            Document document = parse(message);
            logDoc(document);
            return createMessage(document);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    private Message createMessage(Document document) {
        Element element = document.getDocumentElement();
        String nodeName = element.getNodeName();
        if (SAML2_LOGOUT_RESPONSE.equals(nodeName)) {
            return getLogoutResponse(element);
        } else if (SAML2_RESPONSE.equals(nodeName)) {
            return getResponse(element);
        } else {
            return getLogoutRequest(element);
        }
    }

    private LogoutRequest getLogoutRequest(Element element) {
        LogoutRequest logoutRequest = new LogoutRequest();
        NameID nameID = new NameID();
        nameID.setValue(element.getElementsByTagName(SAML2_NAME_ID).item(0).getTextContent());
        logoutRequest.setNameID(nameID);
        return logoutRequest;
    }

    private Response getResponse(Element element) {
        Response response = new Response();
        ArrayList<Assertion> assertions = new ArrayList<Assertion>();
        Assertion e = new Assertion();
        Subject subject = new Subject();
        NameID nameID = new NameID();
        nameID.setValue(element.getElementsByTagName(SAML2_NAME_ID).item(0).getTextContent());
        subject.setNameID(nameID);
        e.setSubject(subject);
        assertions.add(e);
        response.setAssertions(assertions);
        return response;
    }

    private LogoutResponse getLogoutResponse(Element element) {
        LogoutResponse logoutResponse = new LogoutResponse();
        Issuer issuer = new Issuer();
        issuer.setValue(element.getElementsByTagName(SAML2_ISSUER).item(0).getTextContent());
        logoutResponse.setIssuer(issuer);
        StatusCode statusCode = new StatusCode();
        statusCode.setValue(((Element) element.getElementsByTagName(SAML2_STATUS_CODE).item(0)).getAttribute("Value"));
        logoutResponse.setStatusCode(statusCode);
        return logoutResponse;
    }

    private Document parse(String message) throws ParserConfigurationException, SAXException, IOException {
        Document document;
        Inflater inflater = new Inflater(true);
        InflaterInputStream inflaterIn = new InflaterInputStream(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(message)), inflater);
        document = documentBuilder.parse(inflaterIn);
        inflaterIn.close();
        return document;
    }

    private Document getDocument(AuthRequest request) throws IOException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElementNS("urn:oasis:names:tc:SAML:2.0:protocol", SAML_AUTH_REQUEST);
        document.appendChild(root);
        root.setAttribute("AssertionConsumerServiceURL", request.getAssertionConsumerServiceURL());
        root.setAttribute("ForceAuthn", "false");
        root.setAttribute("ID", request.getID());
        root.setAttribute("IsPassive", "false");
        root.setAttribute("IssueInstant", format.format(request.getIssueInstant()));
        root.setAttribute("ProtocolBinding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        root.setAttribute("Version", "2.0");
        Element issuer = document.createElementNS("urn:oasis:names:tc:SAML:2.0:assertion", "samlp:Issuer");
        root.appendChild(issuer);
        issuer.setTextContent(request.getIssuer().getValue());
        Element nameIdPolicy = document.createElementNS("urn:oasis:names:tc:SAML:2.0:protocol", "saml2p:NameIDPolicy");
        root.appendChild(nameIdPolicy);
        nameIdPolicy.setAttribute("AllowCreate", "true");
        nameIdPolicy.setAttribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        nameIdPolicy.setAttribute("SPNameQualifier", "Isser");
        Element requestedAuthnContext = document.createElementNS("urn:oasis:names:tc:SAML:2.0:protocol", "saml2p:RequestedAuthnContext");
        root.appendChild(requestedAuthnContext);
        requestedAuthnContext.setAttribute("Comparison", "exact");
        Element authnContextClassRef = document.createElementNS("urn:oasis:names:tc:SAML:2.0:assertion", "saml:AuthnContextClassRef");
        requestedAuthnContext.appendChild(authnContextClassRef);
        authnContextClassRef.setTextContent("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        return document;
    }

    private Document getDocument(LogoutRequest request) throws IOException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElementNS("urn:oasis:names:tc:SAML:2.0:protocol", SAML2_LOGOUT_REQUEST);
        document.appendChild(root);
        root.setAttribute("ID", createID());
        Date now = new Date();
        root.setAttribute("IssueInstant", format.format(now));
        root.setAttribute("NotOnOrAfter", format.format(now.getTime() + 5 * 60 * 1000));
        root.setAttribute("Reason", request.getReason());
        root.setAttribute("Version", "2.0");
        Element issuer = document.createElementNS("urn:oasis:names:tc:SAML:2.0:assertion", "samlp:Issuer");
        root.appendChild(issuer);
        issuer.setTextContent(request.getIssuer().getValue());
        Element nameId = document.createElementNS("urn:oasis:names:tc:SAML:2.0:assertion", "saml2:NameID");
        root.appendChild(nameId);
        nameId.setAttribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        nameId.setTextContent(request.getNameID().getValue());
        Element sessionIndex = document.createElement("saml2p:SessionIndex");
        root.appendChild(sessionIndex);
        sessionIndex.setTextContent(request.getSessionIndex().getSessionIndex());
        return document;
    }

    private String encode(Document document) throws IOException {
        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOut = new DeflaterOutputStream(out, deflater);
        transform(document, deflaterOut);
        deflaterOut.close();
        return DatatypeConverter.printBase64Binary(out.toByteArray());
    }
    
    private void logDoc(Document doc) {
        if (! LOGGER.isDebugEnabled()) {
            return;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transform(doc, out);
            out.close();
            LOGGER.debug(out.toString());
        } catch (IOException e) {
            // ignore log failure
            e.printStackTrace();
        }
    }

    private void transform(Document document, OutputStream out) throws IOException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(new DOMSource(document), new StreamResult(out));
        } catch (TransformerException e) {
            throw new IOException("transaform document failure", e);
        }
    }

    private String createID() {
        byte[] bytes = new byte[20];
        new Random().nextBytes(bytes);
        char[] charMapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};
        char[] chars = new char[40];
        for (int i = 0; i < bytes.length; i++) {
            int left = (bytes[i] >> 4) & 0x0f;
            int right = bytes[i] & 0x0f;
            chars[i * 2] = charMapping[left];
            chars[i * 2 + 1] = charMapping[right];
        }
        return String.valueOf(chars);
    }

    public String getScheme() {
        return this.scheme;
    }
    
    public void setScheme(String scheme){
        this.scheme = scheme;
    }
}
