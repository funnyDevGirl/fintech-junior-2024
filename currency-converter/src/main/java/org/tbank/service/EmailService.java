package org.tbank.service;

public interface EmailService {

    void sendEmail(String emailAddress, String subject, String message);
}
