package ws.aa;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.authentication.db.auth.*;


public class AABeanHome {
	
	private static final Log log = LogFactory.getLog(AABeanHome.class);
	
	private static AABeanHome aABeanHome = null; 
	
	private EntityManager entityManager;
	
	public AABeanHome() {
		try {
			Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
			entityManager =  (EntityManager) envCtx.lookup("persistence/LogicalName");
			if (entityManager == null)  {
				log.error("AABeanHome(): EntityManager nullo.");
			}
		} catch (NamingException e) {
			log.error("Cattura Entity Manager");
			e.printStackTrace();
		}
	}
	
	public static AABeanHome getInstance() {
		if (aABeanHome == null ) {
			aABeanHome = new AABeanHome();
		}
		return aABeanHome;
	}

	public String getBusinessPartner(String matchType, String matchValue) {
		String q ="SELECT OBJECT(c) FROM auth_wsclient c WHERE c.matchType = :matchType and c.matchValue = :matchValue";
		try {
			Wsclient client = (Wsclient) entityManager.createQuery(q)
				.setParameter("matchType", matchType)
				.setParameter("matchValue", matchValue)
				.getSingleResult();
			return client.getBusinessPartner();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public static String SHA1(String stringa){

		String hashStr = null;
		String password = stringa;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(password.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			hashStr = hash.toString(16);                 
		} catch (NoSuchAlgorithmException e) { 
			e.printStackTrace();
		}
		String str = String.format("%40s", hashStr).replace(' ', '0');
		return(str);
	}
	
	public Wsbasic getWsBasic(String username, String password) {
		
		try {
			Wsbasic wsbasic = getWsBasicFromUsername(username);
			if (wsbasic==null) {
				return null;
			}
			
			String passwordSHA = SHA1(password);
			if(!wsbasic.getPassword().equals(passwordSHA)){
				return null; //validazione fallita
			}
			
			return wsbasic;
			
		} catch (SecurityException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		}
	}

	public Wsbasic getWsBasicFromUsername(String username) {
		String q ="SELECT OBJECT(w) FROM auth_wsbasic w WHERE w.username = :username";
		try {
			return (Wsbasic) entityManager.createQuery(q)
				.setParameter("username", username)
				.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
}
