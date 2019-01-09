//package com.kubera.YoutubeLiveStream;
//
//import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
//import static com.google.api.client.util.Base64.decodeBase64;
//import static java.io.File.separator;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//
//import javax.mail.Address;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.internet.MimeMessage;
//
//import org.apache.commons.mail.util.MimeMessageParser;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.GmailScopes;
//import com.google.api.services.gmail.model.ListMessagesResponse;
//import com.google.api.services.gmail.model.Message;
//import com.google.api.services.gmail.model.MessagePart;
//import com.google.api.services.gmail.model.MessagePartBody;
//import com.google.api.services.gmail.model.ModifyMessageRequest;
//import com.google.api.services.gmail.model.Profile;
//import com.liveyoutube.beans.MessageBean;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.GmailScopes;
//import com.google.api.services.gmail.model.*;
//import com.liveyoutube.beans.MessageBean;
//import org.apache.commons.mail.util.MimeMessageParser;
//
//import javax.mail.Address;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.internet.MimeMessage;
//import java.io.*;
//import java.util.*;
//
//import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
//import static com.google.api.client.util.Base64.decodeBase64;
//import static java.io.File.separator;
//import static org.apache.commons.lang.StringUtils.isEmpty;
//
//public class YoutubeAPICreds {
//
//    private static final String APPLICATION_NAME = "yotuub";
//
//    private static final String ERROR_IN_GET_URL = "Error in getURL";
//
//    private static final String EXCEPTION_IN_GETTING_URL = "exception in getting url ";
//
//    private static final String EXCEPTION_IN_GET_DETAILS_FOR_USER_ID =
//            "Exception in getDetails for userId=";
//
//    /**
//     * Global instance of the JSON factory.
//     */
//
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//
//    private static final String MESSAGE_SIZE_IS = "Message size is ";
//
//    private static final String NO_ATTACHMENTS = "No Attachments";
//
//    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_READONLY);
//
//    private static final Set<String> SCOPES_ALL = GmailScopes.all();
//
//    private static final String SORRY_NOT_AVAILABLE = "sorry, Not available";
//
//    private List<String> fromid = new ArrayList<String>();
//
//    private final String attachmentDownloadPath;
//
//    private final String clientSecretPath;
//
//    public static FileDataStoreFactory DATA_STORE_FACTORY;
//
//    public static HttpTransport HTTP_TRANSPORT;
//
//    public YoutubeAPICreds(final String attachmentDownloadPath, final String clientSecretPath) {
//        if(isEmpty(clientSecretPath)) {
//            this.clientSecretPath = "./";
//        }
//        else {
//            this.clientSecretPath = clientSecretPath;
//        }
//        this.attachmentDownloadPath = attachmentDownloadPath;
//    }
//
//    static {
//        try {
//            HTTP_TRANSPORT = newTrustedTransport();
//        }
//        catch (Throwable t) {
//            System.exit(1);
//        }
//    }
//
//    List<Message> msg;
//
//    private Gmail service;
//
//    private String userEmailId;
//
//    /*
//    Function to get the right credentials and get an authorized service.
//    */
//
//    public Credential authorize() throws IOException {
//        // Load client secrets.
//        InputStream clientSecretIn = new FileInputStream(clientSecretPath);
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(clientSecretIn));
//        GoogleCredential googleCredential =
//                new GoogleCredential.Builder().setJsonFactory(JSON_FACTORY)
//                        .setTransport(HTTP_TRANSPORT).setClientSecrets(clientSecrets).build();
//        // refresh token for statement@perfios.com
//        googleCredential.setRefreshToken("1/-wsXGKkRXecdJ19_PSrVlhPQut9yKI3bSz2uH668Ew4");
//        //        googleCredential.setRefreshToken("1/bzPsPSn6Auc4u3rv_FYqaDBzABQdp7iqtqJiGifkm14");
//        System.out.println("The refresh token is: " + googleCredential.getRefreshToken());
//        return googleCredential;
//    }
//
//    /*
//    Function to initialize the service.
//     */
//
//    public void startGmailService() throws IOException {
//        Credential credential = authorize();
//        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME).build();
//    }
//
//    /*
//    Function to get the list of messages which are unread.
//     */
//
//    public void getProfile() throws IOException {
//        Profile response = service.users().getProfile("me").execute();
//        System.out.println("response is"+response);
//    }
//
//
//    public List<Message> getUnreadMessagesWithAttachments() throws IOException {
//
//        ListMessagesResponse response =
//                service.users().messages().list("me").setQ("subject: SIP Transaction confirmation - 9991050062").execute();
//
//        System.out.println("response length:" + response.getResultSizeEstimate() + "for user: me");
//
//        List<Message> messages = new ArrayList<Message>();
//
//        while (response.getMessages() != null) {
//            messages.addAll(response.getMessages());
//            if (response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().messages().list("me").setPageToken(pageToken).execute();
//            }
//            else {
//                break;
//            }
//        }
//
//        for (Message message : messages) {
//            System.out.println(
//                    "message for user: me" + " with email id: statement@perfios.com is:\n"
//                            + message.toPrettyString());
//        }
//
//        return messages;
//    }
//
//    public MessageBean getMessageDetails(Message msg) throws IOException, MessagingException {
//        Message message =
//                service.users().messages().get("me", msg.getId()).setFormat("raw").execute();
//
//        List<String> fileNameList = new ArrayList<String>();
//
//        try {
//            Message mg = service.users().messages().get("me", msg.getId()).execute();
//            List<MessagePart> parts = mg.getPayload().getParts();
//            for (MessagePart part : parts) {
//                if (part.getFilename() != null && part.getFilename().length() > 0) {
//                    String filename = part.getFilename();
//                    fileNameList.add(filename);
//                }
//
//            }
//        }
//        catch (Exception e) {
//            fileNameList.add(NO_ATTACHMENTS);
//            System.out.println("No Attachments for ");
//        }
//
//        byte[] emailBytes = decodeBase64(message.getRaw());
//
//        Properties props = new Properties();
//
//        Session session = Session.getDefaultInstance(props, null);
//
//        String user = service.users().getProfile("me").getUserId();
//
//        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
//
//        MimeMessageParser parser = new MimeMessageParser(email);
//
//        MessageBean bean = new MessageBean();
//
//        try {
//            Date sentDate = parser.getMimeMessage().getSentDate();
//
//            Date receivedDate = parser.getMimeMessage().getReceivedDate();
//
//            String contentType = parser.getMimeMessage().getContentType();
//
//            String from = parser.getFrom();
//            List<Address> toAddressList = parser.getTo();
//            String subject = parser.getSubject();
//            String content = parser.parse().getPlainContent();
//            Date date = parser.getMimeMessage().getSentDate();
//            String html = parser.parse().getHtmlContent();
//
//            bean.setFrom(from);
//            bean.setToAddressList(toAddressList);
//            bean.setSubject(subject);
//            bean.setSentDate(date);
//            bean.setTextBody(content);
//            bean.setAttachments(fileNameList);
//            bean.setMessage(message);
//            bean.setSentDate(sentDate);
//            bean.setReceivedDate(receivedDate);
//            bean.setContentType(contentType);
//            bean.setHtmlContent(html);
//            for (Address address : toAddressList) {
//                System.out.println("to address is " + address.toString());
//            }
//        }
//        catch (Exception e) {
//            System.out.println(EXCEPTION_IN_GET_DETAILS_FOR_USER_ID + "me");
//        }
//        return bean;
//    }
//
//    public synchronized void downloadAttachments(String messageId) throws IOException {
//        Message message = service.users().messages().get("me", messageId).execute();
//        List<MessagePart> parts = message.getPayload().getParts();
//        for (MessagePart part : parts) {
//            if (part.getFilename() != null && part.getFilename().length() > 0) {
//                File newDir = new File(attachmentDownloadPath);
//                newDir.mkdir();
//
//                System.out.println(
//                        "downloading mails for:" + "sriramstatement@gmail.com" + " with messageID:"
//                                + messageId + " and part ID:" + part.getPartId());
//
//                String user = service.users().getProfile("me").getUserId();
//                String filename = part.getFilename();
//                String attId = part.getBody().getAttachmentId();
//                MessagePartBody attachPart = service.users().messages().attachments().
//                        get("me", messageId, attId).execute();
//
//                byte[] fileByteArray = decodeBase64(attachPart.getData());
//
//                FileOutputStream fileOutFile =
//                        new FileOutputStream(attachmentDownloadPath + separator + filename);
//
//                fileOutFile.write(fileByteArray);
//                fileOutFile.close();
//
//            }
//            else {
//                System.out.println("no attachment found for user:" + "sriramstatement@gmail.com"
//                        + " for messageID:" + messageId + " and part ID:" + part.getPartId());
//            }
//        }
//    }
//
//    public void modifyMessage(String messageId, List<String> labelsToRemove) throws IOException {
//        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(labelsToRemove);
//        Message message = service.users().messages().modify("me", messageId, mods).execute();
//
//        System.out.println("Message has been modified.");
//    }
//
//    public void trashMessage(String msgId) throws IOException {
//        service.users().messages().trash("me", msgId).execute();
//        System.out.println("Message with id: " + msgId + " has been trashed.");
//    }
//
//}
