package ws.controller;

import java.security.Principal;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.X509NameTokenizer;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.util.Store;

import com.authentication.db.auth.*;

import ws.exception.AuthWSException;
import ws.exception.AuthWSFaultBean;
import ws.model.Contacts;
import ws.model.Credentials;
import ws.model.IndividualUser;
import ws.model.LogCallData;
import ws.utils.EmailSender;
import ws.utils.StandardControl;


@SuppressWarnings("deprecation")
@Stateless
public class Functions {
	
	private static final Log log = LogFactory.getLog(Functions.class);

	@EJB
	StandardControl standC;
	
	@EJB
	QueryFunctions queryF;
	
	@EJB
	ContactHome contactHome;
	
	@EJB
	LogcallHome logcallHome;
	
	@EJB
	UserHome userHome;
	
	@EJB
	AccountHome accountHome;
	
	public Functions() {

	}

	public void createAccount(IndividualUser individualUser, 
							  Credentials credentials, 
							  Contacts contacts, 
							  String notificationType,
							  byte[] signedContract
							  ) throws AuthWSException {

		log.info("fase di controllo della presenza dei campi obbligatori");
		
		standC.campiObblig(individualUser,"individualUser");
		standC.campiObblig(credentials,"Credentials");
		standC.campiObblig(contacts,"Contacts");
		standC.campiObblig(signedContract,"signedContract");
		
		standC.campiObblig(credentials.getUsername(),"username dell'oggetto Credentials");
		
		standC.campiObblig(contacts.getEmail(),"email dell'oggetto Contacts");

		log.info("fase di gestione User");
		User user;
		
		String idCode;
		String idType;
		String idCountry;
		
		if(individualUser.getIdUser() == 0){ // non è valorizzato il campo idUser, quindi non si tratta di un utente esistente
			standC.campiObblig(individualUser.getFirstName(),"firstName dell'oggetto User");
			standC.campiObblig(individualUser.getLastName(),"lastName dell'oggetto User");
			standC.campiObblig(individualUser.getIdentificationCode(),"identificationCode dell'oggetto User");
			
			idCode = individualUser.getIdentificationCode().trim().toUpperCase();
			
			if(!standC.isValorized(individualUser.getIdentificationType())) { //di default viene utilizzato il codice fiscale
				idType = "TAXCODE";
			}
			else {
				idType = checkIdentificationType(individualUser.getIdentificationType());
			}
			
			if(!standC.isValorized(individualUser.getIdentificationCountry())) { //di default viene utilizzato il country IT
				idCountry = "IT";
			}
			else {
				idCountry = individualUser.getIdentificationCountry().trim().toUpperCase();
				if(!idCountry.equals("IT") && !queryF.checkCountry(idCountry)) {
					String msg = "Errore nella valorizzazione del country";
					int error = AuthWSFaultBean.ERR_COUNTRY;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
			}
			
			user = queryF.getUser(idCode,idType,idCountry);
			if(user != null) {
				log.info("utente con valori di identificatione " + idCode + "|" + idType + "|" + idCountry + " gia' presente a sistema");
				Account account = queryF.getAccountFromUser(user);
				if(account != null) {
					String msg = "Account gia' presente per il titolare inserito, procedura interrotta";
					int error = AuthWSFaultBean.ERR_ACCOUNT_ALREADY_CREATED;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
			}
			else {
				if(idType.equals("TAXCODE") && idCountry.equals("IT")) {
					standC.checkCodControllo(idCode);
				}
				
				user = new User();
				user.setFirstName(individualUser.getFirstName().trim().toUpperCase());
				user.setLastName(individualUser.getLastName().trim().toUpperCase());
				user.setIdentificationCode(idCode);
				user.setIdentificationType(idType);
				user.setIdentificationCountry(idCountry);
				
				userHome.persist(user);
			}
		}
		else{
			user = userHome.findById(individualUser.getIdUser());
			if(user != null) {
				Account account = queryF.getAccountFromUser(user);
				if(account != null) {
					String msg = "Account gia' presente per il titolare inserito, procedura interrotta";
					int error = AuthWSFaultBean.ERR_ACCOUNT_ALREADY_CREATED;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
			}
			else {
				String msg = "Utente non trovato, controllare l'idUser inserito";
				int error = AuthWSFaultBean.ERR_Q_RESULTLIST_EMPTY;
				log.error(msg + " (" + error + ")");
				throw new AuthWSException(error, msg);
			}
			idCode = user.getIdentificationCode();
			idType = user.getIdentificationType();
			idCountry = user.getIdentificationCountry();
		}
		
		log.info("fase di verifica documento firmato");
		verificaDocumentoFirmato(signedContract,idCode);		
		
		log.info("fase di creazione account");
		if(queryF.checkUsernameAccount(credentials.getUsername().trim())) {
			String msg = "Username gia' presente a sistema, inserimento fallito";
			int error = AuthWSFaultBean.ERR_USERNAME_ALREADY_USED;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
		
		String password = standC.createTemporaryPWD();
		String passwordSHA = standC.SHA1(password);
		
		Notification notification;
		if(standC.isValorized(notificationType)) {
			notification = queryF.getNotificationFromType(notificationType.trim().toUpperCase());
			if(notification == null) {
				String msg = "Tipo di notifica non utilizzabile, le possibili scelte sono: EMAIL, SMS, WHATSAPP, SLACK";
				int error = AuthWSFaultBean.ERR_NOTIFICATION_TYPE;
				log.error(msg + " (" + error + ")");
				throw new AuthWSException(error, msg);
			}
			if(notificationType.equalsIgnoreCase("SMS") || notificationType.equalsIgnoreCase("WHATSAPP")) {
				standC.campiObblig(contacts.getMobile(),"mobile dell'oggetto Contacts");
			}
		}
		else {
			notification = queryF.getNotificationFromType("EMAIL");
		}
				
		Account account = new Account();
		account.setUsername(credentials.getUsername().trim());
		account.setPassword(passwordSHA);
		account.setNotification(notification);
		account.setUser(user);
		account.setRetry(0);
		account.setLastLoginDate(null);
		
		accountHome.persist(account);

		log.info("fase di invio credenziali temporanee");
		Contact contact = new Contact();
		contact.setEmail(contacts.getEmail().trim().toUpperCase());
		
		if(standC.isValorized(contacts.getMobile())) {
			contact.setMobile(contacts.getMobile().trim().toUpperCase());
		}
		
		contact.setUser(user);
		contactHome.persist(contact);
		log.info("aggiunto record nella tabella contatti");
		
		String oggetto = "Creazione Account";
		String testoEmail = "Account creato \n\nUsername: "+ credentials.getUsername().trim() + " \npassword temporanea: " + password;

		EmailSender emailSender = new EmailSender(contacts.getEmail(), oggetto, testoEmail);
		emailSender.inviaEmail();
		log.info("inviata email con la password temporanea all'indirizzo: " + contacts.getEmail());

		log.info("Funzione createAccount portata a termine con successo");
		
	}

	private String checkIdentificationType(String identificationType) throws AuthWSException {
		
		String idType = identificationType.trim().toUpperCase();
		List<String> listaIdType = Fields.getIdentificationTypeAvailable();
		if(listaIdType.contains(idType)) {
			return idType;
		}
		else {
			String msg = "IdentificationType valorizzato in modo errato";
			int error = AuthWSFaultBean.ERR_IDENTIFICATION_TYPE;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
	}

	private void verificaDocumentoFirmato(byte[] signedContract, String idCode) throws AuthWSException {

		try {
			CMSSignedData cmsSignedData = new CMSSignedData(signedContract);
			SignerInformationStore signers = cmsSignedData.getSignerInfos();
			Store certStore = cmsSignedData.getCertificates();
			
			Iterator<?> it = signers.getSigners().iterator();
			while (it.hasNext()) {
				SignerInformation signer = (SignerInformation) it.next();
				Collection<?> certCollection = certStore.getMatches(signer.getSID());
				Iterator<?> certIt = certCollection.iterator();
				X509CertificateHolder certificateHolder = (X509CertificateHolder) certIt.next();
				java.security.cert.X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateHolder);
				
				Principal subjectDNprincipal = certificate.getSubjectDN();
				String subjectDN = subjectDNprincipal.getName();
				String SN = getPartFromDN(subjectDN, "SERIALNUMBER");
				
				if(SN == null) {
					String msg = "Non presente il serial number nel certificato";
					int error = AuthWSFaultBean.ERR_SERIAL_NUMBER_EMPTY;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
				
				log.info(SN);
				String idCodeSigner = "";
				if(SN.contains("-")) {
					idCodeSigner = SN.split("-")[1].toUpperCase().trim();
				}
				else if(SN.contains(":")) {
					idCodeSigner = SN.split(":")[1].toUpperCase().trim();
				}
				else {
					String msg = "Errore nel formato del serial number";
					int error = AuthWSFaultBean.ERR_SERIAL_NUMBER;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
				log.info(idCode);
				log.info(idCodeSigner);
				if(idCode.equalsIgnoreCase(idCodeSigner)) {
					log.info("verifica del file caricato andata a buon fine");
				}
				else {
					String msg = "Errore nel firmatario del documento, non coincidente";
					int error = AuthWSFaultBean.ERR_SIGNER_CONTRACT;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
			}
			
		} catch (CMSException | CertificateException e) {
			String msg = "Errore nella verifica del contratto firmato";
			int error = AuthWSFaultBean.ERR_CHECK_SIGN_CONTRACT;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
		
		
		
	}

	public void loginUser(Credentials credentials) throws AuthWSException {

		log.info("controllo presenza campi obbligatori");
		standC.campiObblig(credentials,"Credentials");
		standC.campiObblig(credentials.getUsername(),"username dell'oggetto Credentials");
		standC.campiObblig(credentials.getPassword(),"password dell'oggetto Credentials");
		
		log.info("controllo presenza dell'account");
		Account account = queryF.getAccountFromUsername(credentials.getUsername().trim());
		if(account == null) {
			String msg = "Errore di login, username o password errata";
			int error = AuthWSFaultBean.ERR_LOGIN;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
		
		if(account.getRetry() == 10) {
			String msg = "Account bloccato";
			int error = AuthWSFaultBean.ERR_LOCKED_ACCOUNT;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
		
		String passwordSHA_DB = account.getPassword();
		String passwordSHA_INPUT = standC.SHA1(credentials.getPassword().trim());
		
		if(!passwordSHA_DB.equals(passwordSHA_INPUT)) {
			int erroriRaggiunti = account.getRetry() + 1;
			account.setRetry(erroriRaggiunti);
			accountHome.merge(account);
			
			String msg = "Errore di login, username o password errata";
			int error = AuthWSFaultBean.ERR_LOGIN;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
		else {
			account.setRetry(0);
			account.setLastLoginDate(new Date());
			accountHome.merge(account);
			log.info("Login avvenuto con successo");
			
			User user = account.getUser();
			Contact contact = queryF.getContactFromUser(user);
			if(contact == null) {
				String msg = "Contatti non trovati, problema nell'invio della notifica";
				int error = AuthWSFaultBean.ERR_NOTIFICATION_CONTACT;
				log.error(msg + " (" + error + ")");
				throw new AuthWSException(error, msg);
			}
			
			if(account.getNotification().getNotification().equals("EMAIL")) {
				if(!standC.isValorized(contact.getEmail())) {
					String msg = "Indirizzo email non valorizzato, problema nell'invio della notifica";
					int error = AuthWSFaultBean.ERR_NOTIFICATION_EMAIL;
					log.error(msg + " (" + error + ")");
					throw new AuthWSException(error, msg);
				}
				String oggetto = "Notifica di Accesso Avvenuto";
				String testoEmail = "E' stata effettuata con successo un'autenticazione con l'account di " + user.getFirstName() + " " + user.getLastName();

				EmailSender emailSender = new EmailSender(contact.getEmail(), oggetto, testoEmail);
				emailSender.inviaEmail();
			}
			else {
				//altre notifiche non implementate
			}
		}

	}
	
	public long gc_log(String codiceLog, String evento, String parametri, int esito){
		
		Logcall logcall = new Logcall();
		logcall.setCodeLog(codiceLog);
		logcall.setEvent(evento);
		logcall.setParameters(standC.encry(parametri));
		logcall.setResult(esito);
		
		logcallHome.persist(logcall);
		log.info("inserito log nel giornale di controllo per l'evento '" + evento + "' con id: " + logcall.getId() + " ed esito: " + esito);
		return logcall.getId();
	}

	public String getPartFromDN(String dn, String dnpart) {
		
		String part = null;
		if((dn != null) && (dnpart != null)) {
			String o;
			dnpart += "=";
			X509NameTokenizer xt = new X509NameTokenizer(dn);
			while(xt.hasMoreTokens()) {
				o = xt.nextToken().trim();
				if((o.length() > dnpart.length()) &&
						o.substring(0, dnpart.length()).equalsIgnoreCase(dnpart)) {
					part = o.substring(dnpart.length());
					
					break;
				}
			}
		}
		return part;
	}
	
	public void test() throws AuthWSException {
		

		
	}

	public String setLogCreateAccount(IndividualUser i, 
									  Credentials cr, 
									  Contacts co,
									  String n, 
									  byte[] sc) {

		String log = "";
		if(i != null) {
			log += i.toString() + ", ";
		}
		else {
			log += "IndividualUser [null], ";
		}
		if(cr != null) {
			log += "Credentials [username: " + cr.getUsername() + ",password: ";
			if(standC.isValorized(cr.getPassword())) {
				log += "***], ";
			}
			else {
				log += "null], ";
			}
		}
		else {
			log += "Credentials [null], ";
		}
		if(co != null) {
			log += co.toString() + ", ";
		}
		else {
			log += "Contacts [null], ";
		}
		if(standC.isValorized(n)) {
			log += "notificationType: " + n + ", ";
		}
		else {
			log += "notificationType: null, ";
		}
		if(sc != null) {
			log += "signedContract: byte[]";
		}
		else {
			log += "signedContract: null";
		}
		return log;
	}

	public String setLogLoginUser(Credentials cr) {

		String log = "";
		if(cr != null) {
			log += "Credentials [username: " + cr.getUsername() + ",password: ";
			if(standC.isValorized(cr.getPassword())) {
				log += "***]";
			}
			else {
				log += "null]";
			}
		}
		else {
			log += "Credentials [null]";
		}
		
		return log;
	}

	public LogCallData getLogCallFromId(int idLogCall) throws AuthWSException {

		LogCallData result = new LogCallData();
		log.info("Ricerca del logcall con id " + idLogCall);
		Logcall logcall = logcallHome.findById(idLogCall);
		if(logcall == null) {
			String msg = "LogCall non trovato, controllare l'id inserito";
			int error = AuthWSFaultBean.ERR_Q_RESULTLIST_EMPTY;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
		
		result.setIdLogCall(idLogCall);
		result.setDateTime(logcall.getTimestamp());
		result.setWebMethod(logcall.getEvent());
		result.setParameters(standC.decry(logcall.getParameters()));
		result.setErrorCode(logcall.getResult());
		if(logcall.getResult() == 0) {
			result.setErrorInfo("NO ERROR");
		}
		else {
			result.setErrorInfo(AuthWSFaultBean.getErrorStr(logcall.getResult()));
		}
		
		log.info("logcall trovato e restituito con successo");
		return result;
	}

}
