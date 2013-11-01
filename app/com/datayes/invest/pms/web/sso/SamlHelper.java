package com.datayes.invest.pms.web.sso;

import com.datayes.paas.sso.User;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.*;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class SamlHelper {

    public static final String SAML_LOGOUT_RESPONSE = "saml2p:LogoutResponse";
    public static final String SAML_LOGOUT_REQUEST = "saml2p:LogoutRequest";

    static {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static AuthnRequest buildAuthRequest(String consumerUrl) {
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "samlp");
        issuer.setValue(consumerUrl);

        NameIDPolicyBuilder nameIdPolicyBuilder = new NameIDPolicyBuilder();
        NameIDPolicy nameIdPolicy = nameIdPolicyBuilder.buildObject();
        nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        nameIdPolicy.setSPNameQualifier("Isser");
        nameIdPolicy.setAllowCreate(Boolean.TRUE);

		/* AuthnContextClass */
        AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
        AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "AuthnContextClassRef", "saml");
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

		/* AuthnContex */
        RequestedAuthnContextBuilder requestedAuthnContextBuilder = new RequestedAuthnContextBuilder();
        RequestedAuthnContext requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);

        /* Creation of AuthRequestObject */
        AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
        AuthnRequest authRequest = authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest", "samlp");
        authRequest.setForceAuthn(Boolean.FALSE);
        authRequest.setIsPassive(Boolean.FALSE);
        authRequest.setIssueInstant(new DateTime());
        authRequest.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        authRequest.setAssertionConsumerServiceURL(consumerUrl);
        authRequest.setIssuer(issuer);
        authRequest.setNameIDPolicy(nameIdPolicy);
        authRequest.setRequestedAuthnContext(requestedAuthnContext);
        authRequest.setVersion(SAMLVersion.VERSION_20);

        String authReqRandomId = Integer.toHexString(
                new Double(ThreadLocalRandom.current().nextDouble()).intValue());
        authRequest.setID(authReqRandomId);

		/* Requesting Attributes. This Index value is registered in the IDP */
        /*String index = Util.getConfiguration(servletConfig, "AttributeConsumingServiceIndex");
        if (index != null && !index.equals("")) {
            authRequest.setAttributeConsumingServiceIndex(Integer.parseInt(index));
        }*/
        return authRequest;
    }

    public static String marshall(RequestAbstractType request) throws IOException {
        try {
            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(request);
            Element dom = marshaller.marshall(request);

            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOut = new DeflaterOutputStream(byteArrayOut, deflater);

            XMLHelper.writeNode(dom, deflaterOut);
            deflaterOut.close();

            String message = Base64.encodeBytes(byteArrayOut.toByteArray(), Base64.DONT_BREAK_LINES);
            return message;
        } catch (MarshallingException e) {
            throw new IOException("encode request failure", e);
        }
    }

    public static LogoutRequest buildLogoutRequest(User user, String consumerUrl) {
        LogoutRequest logoutReq = new LogoutRequestBuilder().buildObject();
        logoutReq.setID(createID());

        DateTime issueInstant = new DateTime();
        logoutReq.setIssueInstant(issueInstant);
        logoutReq.setNotOnOrAfter(new DateTime(issueInstant.getMillis() + 5 * 60 * 1000));

        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(consumerUrl);
        logoutReq.setIssuer(issuer);

        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        nameId.setValue(user.getName());
        logoutReq.setNameID(nameId);

        SessionIndex sessionIndex = new SessionIndexBuilder().buildObject();
        sessionIndex.setSessionIndex(UUID.randomUUID().toString());
        logoutReq.getSessionIndexes().add(sessionIndex);

        logoutReq.setReason("Single Logout");

        return logoutReq;
    }

    public static String createID() {
        byte[] bytes = new byte[20];
        ThreadLocalRandom.current().nextBytes(bytes);
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

    public static XMLObject unmarshall(String message) throws IOException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

            Inflater inflater = new Inflater(true);
            InflaterInputStream inflaterIn = new InflaterInputStream(
                    new ByteArrayInputStream(Base64.decode(message)), inflater);

            Document document = docBuilder.parse(inflaterIn);
            inflaterIn.close();
            Element element = document.getDocumentElement();
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            return unmarshaller.unmarshall(element);
        } catch (Exception e) {
            throw new IOException("unmarshall message failure, message: \n" + message, e);
        }
    }
}
