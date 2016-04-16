package com.hzih.hotbat.utils;


import com.hzih.hotbat.service.entity.ServiceResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-9
 * Time: 下午2:08
 * To change this template use File | Settings | File Templates.
 */
public class ServiceUtils {
    private final static Logger logger = Logger.getLogger(ServiceUtils.class);

    public static ServiceResponse callService(String[][] params) throws IOException {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5 * 1000);
		client.getHttpConnectionManager().getParams().setSoTimeout(5 * 1000);
        String serviceUrl = StringUtils.getServiceUrl();
		PostMethod post = new PostMethod(serviceUrl);
		post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1000 * 5);
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=UTF-8");

		for (String[] param : params) {
			post.addParameter(param[0], param[1]);
		}
		ServiceResponse response = new ServiceResponse();
		int statusCode = client.executeMethod(post);
        response.setCode(statusCode);
        if (statusCode == 200) {
            String data = post.getResponseBodyAsString();
            response.setData(data);
        }
		return response;
	}


	public static InputStream callServiceInputStream(String[][] params) throws IOException {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5 * 1000);
		client.getHttpConnectionManager().getParams().setSoTimeout(5 * 1000);
		String serviceUrl = StringUtils.getServiceUrl();
		PostMethod post = new PostMethod(serviceUrl);
		post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1000 * 5);
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=UTF-8");

		for (String[] param : params) {
			post.addParameter(param[0], param[1]);
		}
		ServiceResponse response = new ServiceResponse();
		int statusCode = client.executeMethod(post);
		response.setCode(statusCode);
		if (statusCode == 200) {
			InputStream data = post.getResponseBodyAsStream();
			if(data!=null){
				return data;
			}
		}
		return null;
	}
}
