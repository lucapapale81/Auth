package ws.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ws.exception.AuthWSException;
import ws.exception.AuthWSFaultBean;

@Stateless
public class StandardControl {
	
    @PersistenceContext(unitName="auth")
    EntityManager em;
	
	private static final Log log = LogFactory.getLog(StandardControl.class);
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String az09 = "0123456789abcdefghijklmnopqrstuvwxyz";
	static final String ALFABETICI = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String NUMERICI = "0123456789";
	static final String ALFABETICI_UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String ALFABETICI_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
	static final String CARATTERI_SPECIALI = "!@#$%^*()_+=";
	static Random rnd = new Random();
	
	public StandardControl(){

	}
	
	/**
	 * controlla se il valore del campo immesso e' presente, in quanto obbligatorio
	 * 
	 * @param str						valore da controllare
	 * @param campo						etichetta del campo da valorizzare
	 * @throws WSException
	 */
	public void campiObblig(String str, String campo) throws AuthWSException {
		if(str == null || str.equals("")){ 
			String msg = "Non tutti i campi obbligatori sono stati valorizzati. Un campo mancante e': " + campo ;
			int error = AuthWSFaultBean.ERR_STRING_EMPTY;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
	}
	
	public void campiObblig(Object obj, String campo) throws AuthWSException {
		if(obj == null){ 
			String msg = "Non tutti gli oggetti obbligatori sono stati valorizzati. Un oggetto mancante e': " + campo ;
			int error = AuthWSFaultBean.ERR_OBJECT_EMPTY;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
	}
	
	public boolean isValorized(String dato){

		if(dato != null && !dato.equals("")){
			return true;
		}
		else{
			return false;
		}

	}
	
	public boolean isAlphaNumeric(String s){
		String pattern= "^[a-zA-Z0-9]*$";
		return s.matches(pattern);
	}
	
	/**
	 * Controlla che il codice fiscale passato in input sia corretto
	 * @param codice_fiscale
	 * @return
	 * @throws WSException
	 */
	public void checkCodControllo(String codice_fiscale) throws AuthWSException{
		if(!isAlphaNumeric(codice_fiscale)){
			String msg = "CODICE FISCALE ERRATO: " + codice_fiscale;
			int error = AuthWSFaultBean.ERR_RANGE_TAXCODE;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}

		String codFisc = codice_fiscale.toUpperCase();

		Map<Character,Integer> charPari = new HashMap<Character, Integer>();
		charPari.put('0', 0);
		charPari.put('1', 1);
		charPari.put('2', 2);
		charPari.put('3', 3);
		charPari.put('4', 4);
		charPari.put('5', 5);
		charPari.put('6', 6);
		charPari.put('7', 7);
		charPari.put('8', 8);
		charPari.put('9', 9);
		charPari.put('A', 0);
		charPari.put('B', 1);
		charPari.put('C', 2);
		charPari.put('D', 3);
		charPari.put('E', 4);
		charPari.put('F', 5);
		charPari.put('G', 6);
		charPari.put('H', 7);
		charPari.put('I', 8);
		charPari.put('J', 9);
		charPari.put('K', 10);
		charPari.put('L', 11);
		charPari.put('M', 12);
		charPari.put('N', 13);
		charPari.put('O', 14);
		charPari.put('P', 15);
		charPari.put('Q', 16);
		charPari.put('R', 17);
		charPari.put('S', 18);
		charPari.put('T', 19);
		charPari.put('U', 20);
		charPari.put('V', 21);
		charPari.put('W', 22);
		charPari.put('X', 23);
		charPari.put('Y', 24);
		charPari.put('Z', 25);

		Map<Character,Integer> charDispari = new HashMap<Character, Integer>();
		charDispari.put('0', 1);
		charDispari.put('1', 0);
		charDispari.put('2', 5);
		charDispari.put('3', 7);
		charDispari.put('4', 9);
		charDispari.put('5', 13);
		charDispari.put('6', 15);
		charDispari.put('7', 17);
		charDispari.put('8', 19);
		charDispari.put('9', 21);
		charDispari.put('A', 1);
		charDispari.put('B', 0);
		charDispari.put('C', 5);
		charDispari.put('D', 7);
		charDispari.put('E', 9);
		charDispari.put('F', 13);
		charDispari.put('G', 15);
		charDispari.put('H', 17);
		charDispari.put('I', 19);
		charDispari.put('J', 21);
		charDispari.put('K', 2);
		charDispari.put('L', 4);
		charDispari.put('M', 18);
		charDispari.put('N', 20);
		charDispari.put('O', 11);
		charDispari.put('P', 3);
		charDispari.put('Q', 6);
		charDispari.put('R', 8);
		charDispari.put('S', 12);
		charDispari.put('T', 14);
		charDispari.put('U', 16);
		charDispari.put('V', 10);
		charDispari.put('W', 22);
		charDispari.put('X', 25);
		charDispari.put('Y', 24);
		charDispari.put('Z', 23);

		Map<Integer,Character> charControl = new HashMap<Integer, Character>();
		charControl.put(0, 'A');
		charControl.put(1, 'B');
		charControl.put(2, 'C');
		charControl.put(3, 'D');
		charControl.put(4, 'E');
		charControl.put(5, 'F');
		charControl.put(6, 'G');
		charControl.put(7, 'H');
		charControl.put(8, 'I');
		charControl.put(9, 'J');
		charControl.put(10, 'K');
		charControl.put(11, 'L');
		charControl.put(12, 'M');
		charControl.put(13, 'N');
		charControl.put(14, 'O');
		charControl.put(15, 'P');
		charControl.put(16, 'Q');
		charControl.put(17, 'R');
		charControl.put(18, 'S');
		charControl.put(19, 'T');
		charControl.put(20, 'U');
		charControl.put(21, 'V');
		charControl.put(22, 'W');
		charControl.put(23, 'X');
		charControl.put(24, 'Y');
		charControl.put(25, 'Z');

		int sommaPari = 0;
		int sommaDispari = 0;

		for (int i=0;i<15;i++){
			if(i%2 == 0)
				sommaDispari += charDispari.get(codFisc.charAt(i));
			else
				sommaPari += charPari.get(codFisc.charAt(i));
		}
		int sommaTotale = sommaPari + sommaDispari;
		int restoSomma = sommaTotale%26;
		char carattereControllo = charControl.get(restoSomma);
		if(carattereControllo != codFisc.charAt(15)) { //codice fiscale errato
			String msg = "CODICE FISCALE ERRATO: " + codice_fiscale;
			int error = AuthWSFaultBean.ERR_RANGE_TAXCODE;
			log.error(msg + " (" + error + ")");
			throw new AuthWSException(error, msg);
		}
	}
	
	public String SHA1(String stringa){

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
	
	public String createTemporaryPWD() {

		StringBuilder sb = new StringBuilder(12);
		sb.append( ALFABETICI_UPPER_CASE.charAt( rnd.nextInt(ALFABETICI_UPPER_CASE.length()) ) );
		sb.append( NUMERICI.charAt( rnd.nextInt(NUMERICI.length()) ) );
		sb.append( ALFABETICI_LOWER_CASE.charAt( rnd.nextInt(ALFABETICI_LOWER_CASE.length()) ) );
		sb.append( CARATTERI_SPECIALI.charAt( rnd.nextInt(CARATTERI_SPECIALI.length()) ) );
		sb.append( NUMERICI.charAt( rnd.nextInt(NUMERICI.length()) ) );
		sb.append( ALFABETICI_UPPER_CASE.charAt( rnd.nextInt(ALFABETICI_UPPER_CASE.length()) ) );
		sb.append( NUMERICI.charAt( rnd.nextInt(NUMERICI.length()) ) );
		sb.append( ALFABETICI_LOWER_CASE.charAt( rnd.nextInt(ALFABETICI_LOWER_CASE.length()) ) );

		return sb.toString();
	}
	
	public String encry(String stringa){
		
		String chiave = "85caw818e7896f24b06f0a11df689fce";
		Query query = em.createNativeQuery("select HEX(AES_ENCRYPT(:stringa,:chiave))");
		query.setParameter("stringa", stringa);
		query.setParameter("chiave", chiave);
		
		return (String) query.getSingleResult();
	}
	
	public String decry(String stringa) throws AuthWSException{
		
		String chiave = "85caw818e7896f24b06f0a11df689fce";
		Query query = em.createNativeQuery("select AES_DECRYPT(UNHEX(:stringa),:chiave)");
		query.setParameter("stringa", stringa);
		query.setParameter("chiave", chiave);
		
		byte[] b = (byte[]) query.getSingleResult();
		String result = null;
		try {
			result = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			String msg = "problema riscontrato all'interno del processo, dovuto ad un'eccezione di sistema";
			int error = AuthWSFaultBean.ERR_EXCEPTION;
			log.error(msg + " (" + error + ")" + e.getMessage());
			throw new AuthWSException(error, msg);
		}
		return result;
	}

}
