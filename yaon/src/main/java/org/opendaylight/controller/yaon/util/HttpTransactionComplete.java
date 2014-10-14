package org.opendaylight.controller.yaon.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class HttpTransactionComplete implements ResponseHandler<String> {


	//private UserData<?> usrData; /* tentative */

	//public HttpTransactionComplete(){
		//usrData = null;
	//}

	//public void setUsrData(UserData<?> usrData) {
		//this.usrData = usrData;
	//}

	@Override
	public String handleResponse(HttpResponse response) throws ClientProtocolException,IOException {

		int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
	}

}
