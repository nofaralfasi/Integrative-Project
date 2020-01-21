package collab.logic.plugins;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {

    private static final String USER_NAME = "yuvalbne";  // GMail user name (just the part before "@gmail.com")
    private static String emailBody = "You have just bought items from our amazing site \n To See all items please go to following link :";
    private static final String SUBJECT = "Buy went succesfully ";
    private static final String FORM = USER_NAME;
	private static final String PASS = PASSWORD;
	
	public static void sendFromGMail(String userMailAddress,String urlAddress) {  
		 emailBody = emailBody + "  \n" + urlAddress;
	   	 String toMailAdress = userMailAddress; 
		 Properties props = System.getProperties();
		  String host = "smtp.gmail.com";
		  props.put("mail.smtp.starttls.enable", "true");
		  props.put("mail.smtp.host", host);
		  props.put("mail.smtp.user", FORM);
		  props.put("mail.smtp.password", PASS);
		  props.put("mail.smtp.port", "587");
		  props.put("mail.smtp.auth", "true");
		  props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		  Session session = Session.getDefaultInstance(props);
		  MimeMessage message = new MimeMessage(session);

		    try {
		        message.setFrom(new InternetAddress(FORM));
		        InternetAddress toAddress = new InternetAddress(toMailAdress);
		        message.addRecipient(Message.RecipientType.TO, toAddress);
		        message.setSubject(SUBJECT);
		        message.setText(emailBody);
		        Transport transport = session.getTransport("smtp");
		        transport.connect(host, FORM, PASS);
		        transport.sendMessage(message, message.getAllRecipients());
		        transport.close();
		    }
		    catch (AddressException ae) {
		        ae.printStackTrace();
		    }
		    catch (MessagingException me) {
		        me.printStackTrace();
		    }
	 }

}