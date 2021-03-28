package ws.model;

import java.util.Date;

public class LogCallData {
	
	private int idLogCall;
	private Date dateTime;
	private String codeLog;
	private String webMethod;
	private String parameters;
	private int errorCode;
	private String errorInfo;
	
	public LogCallData() {
		super();
	}

	public LogCallData(int idLogCall, Date dateTime, String codeLog, String webMethod, String parameters, int errorCode,
			String errorInfo) {
		super();
		this.idLogCall = idLogCall;
		this.dateTime = dateTime;
		this.codeLog = codeLog;
		this.webMethod = webMethod;
		this.parameters = parameters;
		this.errorCode = errorCode;
		this.errorInfo = errorInfo;
	}

	@Override
	public String toString() {
		return "LogCallData [idLogCall=" + idLogCall + ", dateTime=" + dateTime + ", codeLog=" + codeLog
				+ ", webMethod=" + webMethod + ", parameters=" + parameters + ", errorCode=" + errorCode
				+ ", errorInfo=" + errorInfo + "]";
	}

	public int getIdLogCall() {
		return idLogCall;
	}

	public void setIdLogCall(int idLogCall) {
		this.idLogCall = idLogCall;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getCodeLog() {
		return codeLog;
	}

	public void setCodeLog(String codeLog) {
		this.codeLog = codeLog;
	}

	public String getWebMethod() {
		return webMethod;
	}

	public void setWebMethod(String webMethod) {
		this.webMethod = webMethod;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

}
