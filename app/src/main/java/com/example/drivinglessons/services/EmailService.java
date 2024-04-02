package com.example.drivinglessons.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.drivinglessons.util.Constants;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService extends Service
{
    public static final String EMAIL = "email", SUBJECT = "subject", MESSAGE = "message";

    public EmailService() {}

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId)
    {
        String email, subject, message;

        email = intent.getStringExtra(EMAIL);
        subject = intent.getStringExtra(SUBJECT);
        message = intent.getStringExtra(MESSAGE);

        new Thread(() ->
        {
            Session session;

            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "465");

            session = Session.getDefaultInstance(properties, new javax.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(Constants.COMPANY_EMAIL, Constants.COMPANY_PASSWORD);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);

            try
            {
                mimeMessage.setFrom(new InternetAddress(Constants.COMPANY_EMAIL));
                mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(message);
                Transport.send(mimeMessage);
            }
            catch (MessagingException e)
            {
                Log.d("error", e.toString());
                e.printStackTrace();
            }

            stopSelf();

        }).start();

        return START_STICKY;
    }
}