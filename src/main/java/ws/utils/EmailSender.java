package ws.utils;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ws.exception.AuthWSException;
import ws.exception.AuthWSFaultBean;

public class EmailSender {
	
  private static final Log log = LogFactory.getLog(EmailSender.class);

  private String destinatario;
  private String oggetto;
  private String testo;

  public EmailSender(String destinatario, 
                     String oggetto, 
                     String testo){

    this.destinatario = destinatario;
    this.oggetto = oggetto;
    this.testo = testo;
  }

  // Metodo che si occupa dell'invio effettivo della mail
  public void inviaEmail() throws AuthWSException {
	  
	String mittente = "l.papale81@tiscali.it";
	String user = "l.papale81@tiscali.it";
	String password = "*******";
	String host = "smtp.tiscali.it";
	  
    int port = 465; //porta 25 per non usare SSL

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.user", mittente);
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);

    // commentare la riga seguente per non usare SSL 
    props.put("mail.smtp.starttls.enable","true");
    props.put("mail.smtp.socketFactory.port", port);

    // commentare la riga seguente per non usare SSL 
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");

    Session session = Session.getInstance(props, null);
//    session.setDebug(true);

    // Creazione delle BodyParts del messaggio
    MimeBodyPart messageBodyPart1 = new MimeBodyPart();

    try{
      // COSTRUZIONE DEL MESSAGGIO
      Multipart multipart = new MimeMultipart();
      MimeMessage msg = new MimeMessage(session);

      // header del messaggio
      msg.setSubject(oggetto);
      msg.setSentDate(new Date());
      msg.setFrom(new InternetAddress(mittente));

      // destinatario
      msg.addRecipient(Message.RecipientType.TO,
      new InternetAddress(destinatario));

      // corpo del messaggio
      messageBodyPart1.setText(testo);
      multipart.addBodyPart(messageBodyPart1);

      // inserimento delle parti nel messaggio
      msg.setContent(multipart);

      Transport transport = session.getTransport("smtp"); //("smtp") per non usare SSL
      transport.connect(host, user, password);
      transport.sendMessage(msg, msg.getAllRecipients());
      transport.close();

      log.info("Invio dell'email Terminato");

    }catch(AddressException ae) {
    	String msg = "Problema nell'invio della mail (AddressException)";
		int error = AuthWSFaultBean.ERR_SENDING_EMAIL;
		log.error(msg + " (" + error + ") " + ae.getMessage());
		throw new AuthWSException(error, msg);
    }catch(NoSuchProviderException nspe){
    	String msg = "Problema nell'invio della mail (NoSuchProviderException)";
		int error = AuthWSFaultBean.ERR_SENDING_EMAIL;
		log.error(msg + " (" + error + ") " + nspe.getMessage());
		throw new AuthWSException(error, msg);
    }catch(MessagingException me){
    	String msg = "Problema nell'invio della mail";
		int error = AuthWSFaultBean.ERR_SENDING_EMAIL;
		log.error(msg + " (" + error + ") " + me.getMessage());
		throw new AuthWSException(error, msg);
    }
  }
}