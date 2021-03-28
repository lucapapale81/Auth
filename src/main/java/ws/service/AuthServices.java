package ws.service;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.log4j.NDC;

import ws.controller.Functions;
import ws.exception.AuthWSException;
import ws.model.Contacts;
import ws.model.Credentials;
import ws.model.IndividualUser;
import ws.model.LogCallData;

@InInterceptors (interceptors = { "ws.aa.AAInterceptor" })
@WebService
public class AuthServices {
	
	public static String logStringID;
	
    @EJB
    Functions f;
	
	public AuthServices() {
	}
	
	/**
	 * funzione che crea l'account per un utente
	 * 
	 * @param individualUser			oggetto contenente i dati della persona fisica
	 * @param credentials				oggetto contenente le credenziali di accesso
	 * @param contacts					oggetto contenente i dati di contatto dell'utente
	 * @param notificationType			tipo di notifica, valori permessi: EMAIL, SMS, WHATSAPP, SLACK
	 * @param signedContract			byte array del contratto firmato
	 * @throws AuthWSException
	 */
    @WSDLDocumentation("funzione che crea l'account per un utente")
    @WebMethod
    public void createAccount(
            @WebParam(name="individualUser") IndividualUser individualUser,
            @WebParam(name="credentials") Credentials credentials,
            @WebParam(name="contacts") Contacts contacts,
            @WebParam(name="notificationType") String notificationType,
            @WebParam(name="signedContract") byte[] signedContract
            ) throws AuthWSException {

    	String logParameters = f.setLogCreateAccount(individualUser,credentials,contacts,notificationType,signedContract);
    	
        try{
        	f.createAccount(individualUser,credentials,contacts,notificationType,signedContract);
            f.gc_log(NDC.get(), "createAccount", logParameters, 0);
        }
        catch (AuthWSException e) {
            f.gc_log(NDC.get(), "createAccount", logParameters, e.getErrorCode());
            throw new AuthWSException(e.getErrorCode(), e.getMessage());	
        }
        

    }
    
    /**
     * funzione che permette ad un utente di effettuare il login
     * 
     * @param credentials				oggetto contenente le credenziali di accesso
     * @throws AuthWSException
     */
    @WSDLDocumentation("funzione che permette ad un utente di effettuare il login")
    @WebMethod
    public void loginUser(
    		@WebParam(name="credentials") Credentials credentials
            ) throws AuthWSException {

    	String logParameters = f.setLogLoginUser(credentials);
    	
        try{
        	f.loginUser(credentials);
            f.gc_log(NDC.get(), "loginUser", logParameters, 0);
        }
        catch (AuthWSException e) {
            f.gc_log(NDC.get(), "loginUser", logParameters, e.getErrorCode());
            throw new AuthWSException(e.getErrorCode(), e.getMessage());	
        }

    }
    
    /**
     * funzione che estrae un record dalla tabella logcall
     * 
     * @param idLogCall					identificativo della tabella logcall
     * @return
     * @throws AuthWSException
     */
    @WSDLDocumentation("funzione che estrae un record dalla tabella logcall")
    @WebMethod
    public LogCallData getLogCallFromId(
    		@WebParam(name="idLogCall") int idLogCall
            ) throws AuthWSException {

    	return f.getLogCallFromId(idLogCall);

    }

}

