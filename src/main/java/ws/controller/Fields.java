package ws.controller;

import java.util.LinkedList;
import java.util.List;

public class Fields {

	public static List<String> getIdentificationTypeAvailable() {

		LinkedList<String> lista = new LinkedList<String>();
		lista.add("TAXCODE");
		lista.add("PASSPORT");
		lista.add("PERSONAL_NUMBER");
		lista.add("NATIONAL_IDENTITY_CARD");
		return lista;
	}
	
	

}