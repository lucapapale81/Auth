package ws.utils;

import com.slack.api.methods.response.api.ApiTestResponse;
import com.slack.api.Slack;

public class Example {

	public static void main(String[] args) throws Exception {
		Slack slack = Slack.getInstance();
		ApiTestResponse response = slack.methods().apiTest(r -> r.foo("bar"));
		System.out.println(response);

	}
	
}
