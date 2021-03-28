package ws.model;

public class Contacts {
	
	private String email;
	private String mobile;
	
	public Contacts() {
		super();
	}

	public Contacts(String email, String mobile) {
		super();
		this.email = email;
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "Contacts [email=" + email + ", mobile=" + mobile + "]";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}