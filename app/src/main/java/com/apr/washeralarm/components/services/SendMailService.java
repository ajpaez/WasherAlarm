package com.apr.washeralarm.components.services;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.apr.washeralarm.R;
import com.apr.washeralarm.components.SharedPreference.SharedPreferenceReaderSingleton;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailService extends AsyncTask<Void, Void, Void>
{

    //Declaring Variables
    private Context mContext;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;

    private String emailSender;
    private String pass;

    private boolean debuggable = true;

    //Class Constructor
    public SendMailService(Context context, String email, String subject, String message)
    {
        //Initializing variables
        this.mContext = context;
        this.email = email;
        this.subject = subject;
        this.message = message;

        this.emailSender = SharedPreferenceReaderSingleton.getInstance().getEmailSenderNotification();
        this.pass = SharedPreferenceReaderSingleton.getInstance().getPasswordEmailSenderNotification();

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        //Showing a success message
        Toast.makeText(mContext, R.string.notificacion_externa_toast_enviada, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        if (debuggable) {
            props.put("mail.debug", "true");
        }

        // There is something wrong with MailCap, javamail can not find a
        // handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        //Creating a new session
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
        {
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication()
            {

                return new PasswordAuthentication(emailSender, pass);
            }
        });

        try
        {
            //Creating MimeMessage object
            MimeMessage msg  = new MimeMessage(session);

            msg.setSentDate(new Date());

            //Setting sender address
            msg .setFrom(new InternetAddress(this.emailSender));
            //Adding receiver
            msg .addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            msg .setSubject(subject);
            //Adding message
            msg .setText(message);

            msg.setHeader("X-Priority", "1");

            //Sending email
            Transport.send(msg );
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
