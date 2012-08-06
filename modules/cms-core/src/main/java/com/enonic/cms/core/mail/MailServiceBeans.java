package com.enonic.cms.core.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.enonic.cms.store.dao.UserDao;

@Configuration
public class MailServiceBeans
{
    @Value("${cms.admin.newPasswordMailSubject}")
    private String defaultSubjectForNewPasswordEmail;

    @Value("${cms.admin.newPasswordMailBody}")
    private String defaultBodyForNewPasswordEmail;

    @Value("${cms.admin.email}")
    private String adminEmail;

    @Value("${cms.mail.smtpHost}")
    private String smtpHost;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JavaMailSender javaMailSender;

    @Bean
    public SendMailService sendMailService()
    {
        SendMailServiceImpl sendMailService = new SendMailServiceImpl();
        sendMailService.setDefaultSubjectForNewPasswordEmail( defaultSubjectForNewPasswordEmail );
        sendMailService.setDefaultBodyForNewPasswordEmail( defaultBodyForNewPasswordEmail );
        sendMailService.setFromMail( adminEmail );
        sendMailService.setUserDao( userDao );
        sendMailService.setMailSender( javaMailSender );
        return sendMailService;
    }

    @Bean
    public JavaMailSender javaMailSender()
    {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost( smtpHost );
        return javaMailSender;
    }
}

