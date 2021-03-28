package ws.model;

public class IndividualUser {
	private int idUser;
	private String firstName;
	private String lastName;
	
	private String identificationType;
	private String identificationCountry;
	private String identificationCode;
	
	
	public IndividualUser() {
		super();
	}

	public IndividualUser(int idUser, String firstName, String lastName, String identificationType,
			String identificationCountry, String identificationCode) {
		super();
		this.idUser = idUser;
		this.firstName = firstName;
		this.lastName = lastName;
		this.identificationType = identificationType;
		this.identificationCountry = identificationCountry;
		this.identificationCode = identificationCode;
	}

	@Override
	public String toString() {
		return "IndividualUser [idUser=" + idUser + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", identificationType=" + identificationType + ", identificationCountry=" + identificationCountry
				+ ", identificationCode=" + identificationCode + "]";
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getIdentificationCountry() {
		return identificationCountry;
	}

	public void setIdentificationCountry(String identificationCountry) {
		this.identificationCountry = identificationCountry;
	}

	public String getIdentificationCode() {
		return identificationCode;
	}

	public void setIdentificationCode(String identificationCode) {
		this.identificationCode = identificationCode;
	}
	
}