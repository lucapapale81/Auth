package ws.exception;

import java.util.HashMap;
import java.util.Map;

public class AuthWSFaultBean {
	
	public static final int OK 						=  0;
	public static final int ERR_GENERIC 			=  1;
	public static final int ERR_STRING_EMPTY		=  2;
	public static final int ERR_STRING_RANGE		=  3;
	public static final int ERR_Q_RESULTLIST_EMPTY		=  4;
	public static final int ERR_EXCEPTION		=  5;
	public static final int ERR_RANGE_RESULT		=  6;
	public static final int ERR_OBJECT_EMPTY		=  7;
	public static final int ERR_RANGE_TAXCODE		=  8;
	public static final int ERR_ACCOUNT_ALREADY_CREATED		=  9;
	public static final int ERR_LOGIN		=  10;
	public static final int ERR_LOCKED_ACCOUNT		=  11;
	public static final int ERR_NO_PDF_OR_P7M 		=  12;
	public static final int ERR_SENDING_EMAIL 		=  13;
	public static final int ERR_COUNTRY 		=  14;
	public static final int ERR_IDENTIFICATION_TYPE		=  15;
	public static final int ERR_SERIAL_NUMBER		=  16;
	public static final int ERR_SERIAL_NUMBER_EMPTY		=  17;
	public static final int ERR_SIGNER_CONTRACT		=  18;
	public static final int ERR_CHECK_SIGN_CONTRACT		=  19;
	public static final int ERR_USERNAME_ALREADY_USED		=  20;
	public static final int ERR_NOTIFICATION_TYPE		=  21;
	public static final int ERR_NOTIFICATION_CONTACT		=  22;
	public static final int ERR_NOTIFICATION_EMAIL		=  23;

	public static final Map <Integer, String> errors_ITA = new HashMap <Integer, String> ();
	public static final Map <Integer, String> errors_ENG = new HashMap <Integer, String> ();
	
	static {
		errors_ITA.put(OK, 							"Nessun errore riscontrato");
		errors_ITA.put(ERR_GENERIC, 				"Errore non meglio specificato");
		errors_ITA.put(ERR_STRING_EMPTY, 			"Errore dovuto ad un campo non valorizzato");
		errors_ITA.put(ERR_STRING_RANGE,			"Errore per campo/i non valorizzato/i con i valori a lui permessi o con il giusto formato");
		errors_ITA.put(ERR_Q_RESULTLIST_EMPTY,		"Nessun risultato trovato");
		errors_ITA.put(ERR_EXCEPTION,				"Errore dovuto ad un'eccezione riscontrata");
		errors_ITA.put(ERR_RANGE_RESULT,			"Errore dovuto al valore del risultato di una query non appartenente ai valori richiesti");
		errors_ITA.put(ERR_OBJECT_EMPTY, 			"Errore dovuto ad un oggetto non valorizzato");
		errors_ITA.put(ERR_RANGE_TAXCODE, 			"Codice fiscale errato");
		errors_ITA.put(ERR_ACCOUNT_ALREADY_CREATED, "Account gia' presente a sistema");
		errors_ITA.put(ERR_LOGIN, 					"Errore di login");
		errors_ITA.put(ERR_LOCKED_ACCOUNT, 			"Account bloccato");
		errors_ITA.put(ERR_NO_PDF_OR_P7M, 			"Documento non in formato pdf ne' in formato p7m");
		errors_ITA.put(ERR_SENDING_EMAIL, 			"Errore nell'invio della mail");
		errors_ITA.put(ERR_COUNTRY,					"Errore relativo al country");
		errors_ITA.put(ERR_IDENTIFICATION_TYPE,		"IdentificationType valorizzato in modo errato");
		errors_ITA.put(ERR_SERIAL_NUMBER,			"Errore nel formato del serial number del certificato");
		errors_ITA.put(ERR_SERIAL_NUMBER_EMPTY,		"Errore nella valorizzazione del serial number del certificato");
		errors_ITA.put(ERR_SIGNER_CONTRACT,			"Errore nel firmatario del documento, non coincidente");
		errors_ITA.put(ERR_CHECK_SIGN_CONTRACT,		"Errore nella valorizzazione del serial number del certificato");
		errors_ITA.put(ERR_USERNAME_ALREADY_USED,	"Username gia' presente a sistema");
		errors_ITA.put(ERR_NOTIFICATION_TYPE,		"NotificationType valorizzato in modo errato");
		errors_ITA.put(ERR_NOTIFICATION_CONTACT,	"Contatti non trovati, problema nell'invio della notifica");
		errors_ITA.put(ERR_NOTIFICATION_EMAIL,		"Indirizzo email non valorizzato, problema nell'invio della notifica");
		
		//TODO: ENG
		
		errors_ENG.put(OK, 							"OK");
		errors_ENG.put(ERR_GENERIC, 				"Unspecified error");
		errors_ENG.put(ERR_STRING_EMPTY, 			"A field is empty");
		errors_ENG.put(ERR_STRING_RANGE,			"Empty field(s), field(s) not allowed or field(s) in wrong format");
		errors_ENG.put(ERR_Q_RESULTLIST_EMPTY,		"No results found");
		errors_ENG.put(ERR_EXCEPTION,				"Internal exception");
		errors_ENG.put(ERR_RANGE_RESULT,			"Query result is not in the expected range");
		errors_ENG.put(ERR_OBJECT_EMPTY, 			"An object is empty");
		errors_ENG.put(ERR_RANGE_TAXCODE, 			"Incorrect tax code");
		errors_ENG.put(ERR_ACCOUNT_ALREADY_CREATED, "Account already created");
		errors_ENG.put(ERR_LOGIN, 					"Login Error");
		errors_ENG.put(ERR_LOCKED_ACCOUNT, 			"Locked Account");
		errors_ENG.put(ERR_NO_PDF_OR_P7M, 			"Document not pdf nor p7m format");
		errors_ENG.put(ERR_SENDING_EMAIL, 			"Error in sending email");
		errors_ENG.put(ERR_COUNTRY,					"Country related error");
		errors_ENG.put(ERR_IDENTIFICATION_TYPE,		"IdentificationType entered incorrectly");
		errors_ENG.put(ERR_SERIAL_NUMBER,			"Certificate serial number format error");
		errors_ENG.put(ERR_SERIAL_NUMBER_EMPTY,		"Error in validating the serial number of the certificate");
		errors_ENG.put(ERR_SIGNER_CONTRACT,			"Error in the document signer, not coincident");
		errors_ENG.put(ERR_CHECK_SIGN_CONTRACT,		"Error in validating the serial number of the certificate");
		errors_ENG.put(ERR_USERNAME_ALREADY_USED,	"Username already present in the system");
		errors_ENG.put(ERR_NOTIFICATION_TYPE,		"NotificationType entered incorrectly");
		errors_ENG.put(ERR_NOTIFICATION_CONTACT,	"Contacts not found, problem sending the notification");
		errors_ENG.put(ERR_NOTIFICATION_EMAIL,		"Email address not entered, problem in sending the notification");

	}

	public static String getErrorStr(int code) {
		return errors_ITA.get(code);
	}
	
	public static String getErrorStrENG(int code) {
		return errors_ENG.get(code);
	}
	
	private int error;	
	private String message;
	
	public AuthWSFaultBean() { }
		
	public AuthWSFaultBean(int error, String message) {
		setError(error);
		setMessage(message);
	}

	public AuthWSFaultBean(int error) {
		setError(error);
		setMessage(getErrorStr(error));
	}
	
	public String getMessage() { return message; }
	public void setMessage(String message) {  this.message = message;}

	public int getError() 			{ return error; }
	public void setError(int error) { this.error = error; }
	
}


