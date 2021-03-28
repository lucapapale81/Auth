package ws.controller;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.authentication.db.auth.Account;
import com.authentication.db.auth.Contact;
import com.authentication.db.auth.Country;
import com.authentication.db.auth.Notification;
import com.authentication.db.auth.User;


@Stateless
public class QueryFunctions {
	
    @PersistenceContext(unitName="auth")
    EntityManager em;
	
	private static final Log log = LogFactory.getLog(QueryFunctions.class);
	
	public QueryFunctions() {
		
	}
	
	public Account getAccountFromUser(User user) {
		
		Query q = em.createQuery("FROM auth_account a WHERE a.user =:user");
        q.setParameter("user", user);
        if(q.getResultList().isEmpty()) {
            return null;
        }
        return (Account) q.getSingleResult();
	}
	
	public Account getAccountFromUsername(String username) {
		
		Query q = em.createQuery("FROM auth_account a WHERE a.username =:username");
        q.setParameter("username", username);
        if(q.getResultList().isEmpty()) {
            return null;
        }
        return (Account) q.getSingleResult();
	}

	public User getUser(String identificationCode, String identificationType, String identificationCountry) {
		
		Query q = em.createQuery("FROM auth_user WHERE identificationCode =:identificationCode AND identificationType = :identificationType AND identificationCountry = :identificationCountry");
        q.setParameter("identificationCode", identificationCode);
        q.setParameter("identificationType", identificationType);
        q.setParameter("identificationCountry", identificationCountry);
        if(q.getResultList().isEmpty()) {
            return null;
        }
        return (User) q.getSingleResult();
	}

	public boolean checkUsernameAccount(String username) {
		
		Query q = em.createQuery("FROM auth_account WHERE username =:username");
        q.setParameter("username", username);
        if(q.getResultList().isEmpty()) {
            return false;
        }
        else {
        	return true;
        }
	}
	
	public boolean checkCountry(String sigla) {
		
		Query q = em.createQuery("FROM auth_country WHERE code =:sigla");
        q.setParameter("sigla", sigla);
        if(q.getResultList().isEmpty()) {
            return false;
        }
        else {
        	Country c = (Country) q.getSingleResult();
        	log.info("country: " + c.getCountry());
        	return true;
        }
	}

	public Notification getNotificationFromType(String notificationType) {
		
		Query q = em.createQuery("FROM auth_notification WHERE notification =:notificationType");
        q.setParameter("notificationType", notificationType);
        if(q.getResultList().isEmpty()) {
            return null;
        }
        return (Notification) q.getSingleResult();
	}

	public Contact getContactFromUser(User user) {
		
		Query q = em.createQuery("FROM auth_contact WHERE user =:user");
        q.setParameter("user", user);
        if(q.getResultList().isEmpty()) {
            return null;
        }
        return (Contact) q.getResultList().get(0); //non messo il getSingleResult perche si potrebbe pensare in futuro ad associare piu contatti ad ogni utente
	}

}
