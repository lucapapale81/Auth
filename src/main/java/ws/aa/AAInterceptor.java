package ws.aa;

import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.transport.TLSSessionInfo;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.MethodDispatcher;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.bouncycastle.asn1.x509.X509NameTokenizer;

import com.authentication.db.auth.*;

import ws.aa.AAModel.PeerAuthorizationMethod;
import ws.exception.AuthWSException;
import ws.exception.AuthWSFaultBean;
import ws.service.AuthServices;



@SuppressWarnings("deprecation")
public class AAInterceptor extends AbstractPhaseInterceptor <Message>  {
	
	private static final Log log = LogFactory.getLog(AAInterceptor.class);
	
	public AAInterceptor() {
		super(Phase.PRE_INVOKE);
	}
	
	protected Method getTargetMethod(Message m) {
		Exchange exchange = m.getExchange();
		BindingOperationInfo bop = exchange.get(BindingOperationInfo.class);
		MethodDispatcher md = (MethodDispatcher) exchange.get(Service.class).get(MethodDispatcher.class.getName());
		Method method = md.getMethod(bop);
		if (method == null) {
			String msg = "Metodo non disponibile";
			log.error(msg);
			throw new AccessDeniedException(msg);
		}
		return method;
	}

	private Fault accessDeniedException(String message) {
		return new Fault(new AuthWSException(AuthWSFaultBean.ERR_GENERIC, message));
	}
	
	private String getClientCN(Message message) {
	   	TLSSessionInfo tlsInfo = message.get(TLSSessionInfo.class);
		if (tlsInfo != null && 
			tlsInfo.getPeerCertificates() != null && 
			tlsInfo.getPeerCertificates().length > 0 && 
			(tlsInfo.getPeerCertificates()[0] instanceof X509Certificate)) {
			X509Certificate cert = (X509Certificate) tlsInfo.getPeerCertificates()[0];
			String subjectDN = cert.getSubjectDN().getName();
			return getPartFromDN(subjectDN, "CN");
		} else
			return null;
	}
	
	private static String getPartFromDN(String dn, String dnpart) {
		
		String part = null;
		if ((dn != null) && (dnpart != null)) {
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
	
	private static String getClientRemoteAddr(Message message) {
		
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
    	if (request == null) {
    		String msg = "Errore nel servizio di gestione richieste";
			log.error(msg);
			throw new AccessDeniedException(msg);
		}
	    return request.getRemoteAddr();
	}
	@Override
    public void handleMessage(Message message) throws Fault {
		String method =  getTargetMethod(message).getName();
		String msg;
		
		generaLogID();
		
		boolean authorizedSystem = false;
		String matchType = null; 
		String matchValue = null;
		String businessPartner = null;
		
		//
    	// verifico autorizzazione al metodo per il richiedente identificato per certificato SSL
    	//  
    	if (!authorizedSystem) {
    		String cn = getClientCN(message);
    		log.info(cn);
    		if (cn != null) {
				String bp = AABeanHome.getInstance().getBusinessPartner(PeerAuthorizationMethod.ssl.name(), cn);
				if (bp != null) {
					authorizedSystem = true;
					matchType = PeerAuthorizationMethod.ssl.name();
					matchValue = cn;
					businessPartner = bp;
				}
			} 
		}
		
		//
    	// verifico autorizzazione al metodo per il richiedente identificato per ip
    	//  
		if (!authorizedSystem) {
			String remoteAddr = getClientRemoteAddr(message);
			log.info(remoteAddr);

	    	String bp = AABeanHome.getInstance().getBusinessPartner(PeerAuthorizationMethod.ip.name(), remoteAddr);
			if (bp != null) {
				authorizedSystem = true;
				matchType = PeerAuthorizationMethod.ip.name();
				matchValue = remoteAddr;
				businessPartner = bp;
				log.info(businessPartner);
			}
		}

    	if (!authorizedSystem) {
    		msg = String.format("Sistema non autorizzato alla chiamata del webmethod %s", method);
    		log.info(msg);
    		throw accessDeniedException(msg);
    	}
    	
		msg = "Autorizzazione concessa al sistema " + businessPartner + ". Metodo: " + method + ", tipo: " + matchType + ", valore: " + matchValue;
		log.info(msg);
		 
		boolean authenticatedUser = false;
		String username = null;
		String password = null;
		Wsbasic wsbasic = null;
		
		//
    	// verifico autorizzazione al metodo per il richiedente identificato per basic authentication
    	//  
		AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);
        if (policy != null) {
        	username = policy.getUserName();
        	log.info(username);
        	
			password = policy.getPassword();

			if (username == null || password == null) {
				msg = "Username o password nulli";
				log.info(msg);
				throw accessDeniedException(msg);
			}
			wsbasic = AABeanHome.getInstance().getWsBasic(username, password);
			if (wsbasic == null) {
				msg = "Username o password errati";
				log.info(msg);
				throw accessDeniedException(msg);
			}
			else {
				authenticatedUser = true;
			}
			
			msg = "Utente con username " + username + " autenticato.";
			log.info(msg);
        	
		}
		
		if(!authenticatedUser) {
			msg = String.format("Utente non autorizzato alla chiamata del webmethod %s", method);
			log.info(msg);
			
			throw accessDeniedException(msg);
		}
		
		logMethodCallString(message, getTargetMethod(message) , username, businessPartner);
		
	}
	
	public static void logMethodCallString(Message message, Method method, String user, String sistema) {
		if (method != null) {
			String methodStr = method.getName();
			List <MessagePartInfo> parts = message.getExchange().getBindingOperationInfo().getInput().getMessageParts();

			String params = "";

			for (int i = 0; i < parts.size(); i++) {
				MessagePartInfo part = (MessagePartInfo) parts.get(i);
				String paramName = part.getConcreteName().getLocalPart();
				Object paramValue = ((MessageContentsList) message.getContent(List.class)).get(i);
				String paramStr = "";

				if(paramValue == null){
					paramStr = "null";
					params += String.format("%s: %s", paramName, paramStr);
					if(i != (parts.size()-1))
						params += ", ";
					continue;
				}
				
				if (paramValue instanceof String) {
					paramStr = (String) paramValue;
				} else if (paramValue instanceof Integer) {
					paramStr = ((Integer) paramValue).toString();
				} else if (paramValue instanceof Boolean) {
					paramStr = ((Boolean) paramValue).toString();
				} else if (paramValue instanceof byte[]) {
					paramStr = "byte array";
				} else if (paramValue instanceof ws.model.Credentials) {
					paramStr = ((ws.model.Credentials) paramValue).toString();
				} else if (paramValue instanceof ws.model.Contacts) {
					paramStr = ((ws.model.Contacts) paramValue).toString();
				} else if (paramValue instanceof ws.model.IndividualUser) {
					paramStr = ((ws.model.IndividualUser) paramValue).toString();
				}
				
				if(paramName.equals("newPassword"))
					paramStr = "***";
				if(paramName.equals("oldPassword"))
					paramStr = "***";
				if(paramName.equals("password"))
					paramStr = "***";
				
				params += String.format("%s: %s", paramName, paramStr);
				if(i != (parts.size()-1))
					params += ", ";
			}

			String msg = String.format("Src Metodo: %s, Params: (%s)", methodStr, params);
			log.info(msg);
			
			String evento = "Autorizzazione concessa al sistema " + sistema;
			String violazione = null; 
			String timestamp = generaTimeStamp();
			String metodo = methodStr; 
			String username = user;
			String msg2 = String.format("[event=\"%s\" violation=\"%s\" timestamp=\"%s\" job_name=\"%s\" user_name=\"%s\"]", evento, violazione, timestamp, metodo, username);
		
			log.info(msg2);
		}
	}
	
	private String stringLog(){
		int SUP = 999999999;
		int INF = 100000000;
		int casuale = (int)((SUP-INF+1)*Math.random())+INF;
		return Integer.toString(casuale);
	}
	private void generaLogID() {
		
		String logID = stringLog();
		org.apache.log4j.NDC.clear();
		org.apache.log4j.NDC.push(logID);
		AuthServices.logStringID = logID;
	}
	
	private static String generaTimeStamp(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String dataAttuale = dateFormat.format(c.getTime());
		return dataAttuale += "000";
	}
	
}
