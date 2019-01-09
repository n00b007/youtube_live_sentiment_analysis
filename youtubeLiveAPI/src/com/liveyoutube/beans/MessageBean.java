//package com.kubera.beans;
//
//import com.google.api.client.util.Joiner;
//import com.google.api.services.gmail.model.Message;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.mail.Address;
//
//public class MessageBean {
//
//    private List<String> attachments;
//
//    private List<Address> toAddressList;
//
//    private String textBody;
//
//    private Message message;
//
//    private Date sentDate;
//
//    private Date receivedDate;
//
//    private String contentType;
//
//    public String getContentType() {
//        return contentType;
//    }
//
//    public void setContentType(String contentType) {
//        this.contentType = contentType;
//    }
//
//    public String getHtmlContent() {
//        return htmlContent;
//    }
//
//    public void setHtmlContent(String htmlContent) {
//        this.htmlContent = htmlContent;
//    }
//
//    private String htmlContent;
//
//
//    public Date getReceivedDate() {
//        return receivedDate;
//    }
//
//    public List<Address> getToAddressList() {
//        return toAddressList;
//    }
//
//    public void setToAddressList(final List<Address> toAddressList) {
//        this.toAddressList = toAddressList;
//    }
//
//    public void setReceivedDate(Date receivedDate) {
//        this.receivedDate = receivedDate;
//    }
//
//    private String from;
//
//    private String subject;
//
//    public List<String> getAttachments() {
//        return attachments;
//    }
//
//    public String getAttachmentsData() {
//        return Joiner.on(',').join(attachments);
//    }
//
//    public Date getSentDate() {
//        return sentDate;
//    }
//
//    public String getTextBody() { return textBody; }
//
//    public void setTextBody(String textBody) { this.textBody = textBody; }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public String getSubject() {
//        return subject;
//    }
//
//    public void setAttachments(List<String> attachments) {
//        this.attachments = attachments;
//    }
//
//    public void setSentDate(Date sentDate) {
//        this.sentDate = sentDate;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    public void setMessage (Message message) { this.message = message; }
//
//    public Message getMessage() { return message; }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("from:").append(getFrom()).append("\n")
//                .append("to:").append(getToAddressList().get(0)).append("\n")
//                    .append("subject:").append(getSubject()).append("\n")
//                .append("sentDate:").append(getSentDate()).append("\n")
//                .append("receivedDate:").append(getReceivedDate()).append("\n")
//                .append("attachments:").append(getAttachmentsData()).append("\n")
//                .append("body:").append(getTextBody()).append("\n")
//                .append("contentType:").append(getContentType()).append("\n")
//                .append("htmlContent:").append(getHtmlContent()).append("\n");
//        return sb.toString();
//
//    }
//}
