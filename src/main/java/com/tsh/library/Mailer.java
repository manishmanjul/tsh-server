package com.tsh.library;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

@Component
public class Mailer {

	private Properties props = null;
	private Session session = null;
	private Message message = null;

	public Mailer() {
		this.initialize();
	}

	public static void main(String args[]) {
		Mailer mailer = new Mailer();
		try {

			mailer.sendAsHTML("manjul.manish@gmail.com", "Test Mails",
					"<div id=\"2178\" hidden=\"\" style=\"border: 1px solid rgb(224, 222, 222); width: 40%;\" bis_skin_checked=\"1\"><section style=\"border: 1px solid rgb(224, 222, 222); font-size: 0px; padding-top: 4px; padding-bottom: 3px; background-color: rgb(11, 102, 138); color: white;\"><span style=\"font-size: 16px; font-weight: bold;\">[26-11-2020] - </span><span style=\"padding: 0%; font-size: 20px; font-weight: bold;\">Term 4 Week 6 - </span><span style=\"font-size: 16px; font-weight: bold;\">G -4 Maths</span></section><section style=\"border: 1px solid rgb(224, 222, 222); background: rgb(248, 245, 244);\"><p style=\"font-size: 16px; font-weight: bold;\"><span>REVISION</span></p><div bis_skin_checked=\"1\" style=\"width: 30%; border: 1px solid red;\"></div><p><span>Sound - Learned the concept but needs support sometimes</span></p><p style=\"color: red;\">Teacher - <span style=\"color: black;\">Answered 90% of the questions</span></p></section><section style=\"border: 1px solid rgb(224, 222, 222);\"><p style=\"font-size: 16px; font-weight: bold;\"><span>CLASSWORK</span></p><div bis_skin_checked=\"1\" style=\"width: 30%; border: 1px solid red;\"></div><p><span>Incomplete - Did not complete all questions</span></p><p style=\"color: red;\">Teacher - <span style=\"color: black;\">Could only finish 70% of the questions</span></p></section><section style=\"border: 1px solid rgb(224, 222, 222); background: rgb(248, 245, 244);\"><p style=\"font-size: 16px; font-weight: bold;\"><span>HOMEWORK</span></p><div bis_skin_checked=\"1\" style=\"width: 30%; border: 1px solid red;\"></div><p><span>Outstanding - Fully understand the concept</span></p><p style=\"color: red;\">Teacher - <span style=\"color: black;\">good</span></p></section><section style=\"border: 1px solid rgb(224, 222, 222);\"><p style=\"font-size: 16px; font-weight: bold;\"><span>ASSESSMENT</span></p><div bis_skin_checked=\"1\" style=\"width: 30%; border: 1px solid red;\"></div><p><span>Skip Assessment - Topic was not an Assessment</span></p><p style=\"color: red;\">Teacher - <span style=\"color: black;\">No Assessments done</span></p></section><section style=\"border: 1px solid rgb(224, 222, 222); background: rgb(248, 245, 244); text-align: center; justify-content: center;\"><p style=\"font-size: 12px; font-weight: bold;\"><span>Feedback By: <span style=\"color: red;\">Manish</span></span></p></section></div>");
			System.out.println("Mail sent successfully...");
		} catch (MessagingException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.office365.com");
		props.put("mail.smtp.port", "587");
	}

	private void login() {
		session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("tshcalendar@thestudyhouse.com.au", "Calendar2769");
			}
		});
	}

	private void createMessage() throws AddressException, MessagingException, UnsupportedEncodingException {
		if (session == null)
			this.login();

		this.message = new MimeMessage(this.session);
		this.message.setFrom(new InternetAddress("tshcalendar@thestudyhouse.com.au", "TSH House"));
	}

	public boolean sendMail(String To, String subject, String body)
			throws AddressException, MessagingException, UnsupportedEncodingException {

		this.createMessage();
		this.message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(To)[0]);
		this.message.setSubject(subject);
		this.message.setText(body);

		Transport.send(message);

		return true;
	}

	public boolean sendAsHTML(String To, String subject, String HTMLContent)
			throws MessagingException, UnsupportedEncodingException {

		this.createMessage();
		this.message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(To)[0]);
		this.message.setSubject(subject);
		this.message.setContent(HTMLContent, "text/html");

		Transport.send(message);
		return true;
	}

	public void setTo(String To) throws AddressException, MessagingException {
		this.message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(To)[0]);
	}

	public void setSubject(String subject) throws MessagingException {
		this.message.setSubject(subject);
	}

	public void setBody(String text) throws MessagingException {
		this.message.setText(text);
	}

}
