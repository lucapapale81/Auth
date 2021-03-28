package ws.exception;

import javax.xml.ws.WebFault;

@WebFault
public class AuthWSException extends Exception {

	private static final long serialVersionUID = 5652257363630125744L;
	private AuthWSFaultBean faultInfo;
	private int errorCode;

	public void setFaultInfo(AuthWSFaultBean faultInfo) {
		this.faultInfo = faultInfo;
	}

	public AuthWSException() {}
	
	public AuthWSException(AuthWSFaultBean faultInfo) {
		super(faultInfo.getMessage());
	}
	
	public AuthWSException(String message) {
	    super(message);
	}
	
	public AuthWSException(String message, AuthWSFaultBean faultInfo) {
	    super(message);
	    this.faultInfo = faultInfo;
	}

	public AuthWSException(String message, AuthWSFaultBean faultInfo, Throwable cause) {
	    super(message, cause);
	    this.faultInfo = faultInfo;
	}

	public AuthWSFaultBean getFaultInfo() {
	    return faultInfo;
	}
	
	public AuthWSException(int error, String message) {
		super(message);
		setErrorCode(error);
		faultInfo = new AuthWSFaultBean();
		faultInfo.setError(error);
		faultInfo.setMessage(message);
	}
	
	public AuthWSException(int error) {
		super();
		setErrorCode(error);
		faultInfo = new AuthWSFaultBean(error);
		faultInfo.setError(error);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}

