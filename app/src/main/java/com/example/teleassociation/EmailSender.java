package com.example.teleassociation;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void sendEmail(String recipientEmail, String emailSubject, String emailMessage) {
        new SendEmailTask(recipientEmail, emailSubject, emailMessage).execute();
    }

    private static class SendEmailTask extends AsyncTask<Void, Void, Void> {
        private String recipientEmail;
        private String emailSubject;
        private String emailMessage;

        public SendEmailTask(String recipientEmail, String emailSubject, String emailMessage) {
            this.recipientEmail = recipientEmail;
            this.emailSubject = emailSubject;
            this.emailMessage = emailMessage;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("msg-test", "Ingreso al send email");

            // Configurar propiedades del servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com"); // Reemplaza con tu servidor SMTP
            props.put("mail.smtp.port", "587"); // Puerto SMTP
            props.put("mail.smtp.ssl.trust", "*"); // Desactivar la verificación SSL

            // Configurar la sesión de JavaMail
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("miguelac1112@gmail.com", "mxwekzrbrtduizfg");
                }
            });

            Log.d("msg-test", "Se configuro la sesion de email");

            try {
                Log.d("msg-test", "Entrar al try");
                // Crear un objeto MimeMessage
                MimeMessage mimeMessage = new MimeMessage(session);

                // Establecer dirección de correo electrónico del remitente
                mimeMessage.setFrom(new InternetAddress("ahumadac.m@pucp.edu.pe"));

                // Establecer dirección de correo electrónico del destinatario
                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientEmail));

                // Establecer el asunto del correo
                mimeMessage.setSubject(emailSubject);

                // Establecer el contenido del correo
                mimeMessage.setText(emailMessage+"\n\nSaludos,\nMesa Directiva Aitel 2023");
                Log.d("msg-test", "Antes de envio de correo");
                // Enviar el correo
                Transport.send(mimeMessage);

            } catch (MessagingException e) {
                Log.d("msg-test", "Excepcion en envio de correo: " + e);
                e.printStackTrace();
            }
            return null;
        }
    }
}

