package com.qkwl.common.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.SortedMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * HTTP请求池
 * @author ZKF
 */
public class HttpPoolingManager {

	private static String defaultCharset				= "UTF-8";	// 默认编码
	
	private int defaultConnectionTimeout    			= 30000;		// 连接超时
    private int defaultSocketTimeout    				= 30000;	// 读取超时,回应超时时间
    private int defaultConnectionRequestTimeout   		= 8000;		// 从连接池获取连接实例的超时
	private int defaultMaxPerRoute 						= 80; 		// 对每个指定连接的服务器（指定的ip）可以创建并发50socket进行访问（路由连接数）
	private int maxTotal 								= 1000; 	// 最大

	private static PoolingHttpClientConnectionManager 	connMgr;
	private static RequestConfig 						requestConfig;
	private static HttpPoolingManager 					hpm;
	
	private String keyStoreFilePath;								// PK12文件路径
	private String keyStorePassword;								// PK12文件密码

	public synchronized static HttpPoolingManager getInstance() {
		if (hpm == null) {
			hpm = new HttpPoolingManager();
		}
		return hpm;
	}
	
	private HttpPoolingManager() {
		connMgr = new PoolingHttpClientConnectionManager();
		connMgr.setMaxTotal(maxTotal);
		connMgr.setDefaultMaxPerRoute(defaultMaxPerRoute);
		
		RequestConfig.Builder configBuilder = RequestConfig.custom();  
        configBuilder.setConnectTimeout(defaultConnectionTimeout);					// 设置连接超时  
        configBuilder.setSocketTimeout(defaultSocketTimeout);						// 设置读取超时  
        configBuilder.setConnectionRequestTimeout(defaultConnectionRequestTimeout); // 设置从连接池获取连接实例的超时  
        requestConfig = configBuilder.build();
	}

	/**
	 * @Title: getHttpClient
	 * @Description: TODO(默认)
	 * @return CloseableHttpClient  返回类型
	 */
	public CloseableHttpClient getHttpClient() {
		return  HttpClients.custom()
				.setConnectionManager(connMgr)
				.setDefaultRequestConfig(requestConfig)
				.build();
	}

	/**
	 * @Title: getHttpClientSSL
	 * @Description: TODO(服务器SSL)
	 * @return CloseableHttpClient  返回类型
	 */
	public CloseableHttpClient getHttpClientSSL() {
		return 	HttpClients.custom()
				.setSSLSocketFactory(MySSLSocketFactory.getInstance())
				.setDefaultRequestConfig(requestConfig)
				.build();
	}
	
	/**
	 * @Title: getHttpClientSSLByKeyStore
	 * @Description: TODO(双向SSL)
	 * @return CloseableHttpClient  返回类型
	 */
	public CloseableHttpClient getHttpClientSSLByKeyStore() {
		KeyStore trustStore = HttpUtil.loadKeyStore(keyStoreFilePath, keyStorePassword,"PKCS12");
		try {
			
			SSLContext sslcontext = SSLContexts.custom()
					.loadKeyMaterial(trustStore, keyStorePassword.toCharArray())
					.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1" },
					null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			return 	HttpClients.custom()
					.setSSLSocketFactory(sslsf)
					.setDefaultRequestConfig(requestConfig)
					.build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @Description: TODO(POST，参数是Map)
	 * @param apiUrl URL地址
	 * @param sslType HttpSSLType
	 * @return String  返回String,
	 */
	public String post(String apiUrl, HttpSSLType sslType, String xml) {
		String responseBody = "";
		HttpPost httpPost = new HttpPost(apiUrl);

		CloseableHttpClient httpClient = null;
		switch (sslType) {
			case NONE:
				httpClient = getHttpClient(); 				break;
			case ONEWAY:
				httpClient = getHttpClientSSL(); 			break;
			case TWOWAY:
				httpClient = getHttpClientSSLByKeyStore(); 	break;
		}
		CloseableHttpResponse response = null;
		try {
			// POST参数    
			if(null != xml && !xml.isEmpty()){
				StringEntity entity = new StringEntity(xml, defaultCharset);
				httpPost.setHeader("Content-Type", "text/xml;charset=" + defaultCharset);
				httpPost.setEntity(entity); 
			}
			
			response = httpClient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				responseBody = entity != null ? EntityUtils.toString(entity, defaultCharset) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status + ",apiUrl=" + apiUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseBody;
	}

	/**
	 * @Description: TODO(POST，参数是Map)
	 * @param apiUrl URL地址
	 * @param sslType HttpSSLType
	 * @param contentType 请求类型
	 * @param charset 请求编码
	 * @return String  返回String,
	 */
	public String post(String apiUrl, HttpSSLType sslType, String data, String contentType, String charset) {
		String responseBody = "";
		HttpPost httpPost = new HttpPost(apiUrl);

		CloseableHttpClient httpClient = null;
		switch (sslType) {
			case NONE:
				httpClient = getHttpClient(); 				break;
			case ONEWAY:
				httpClient = getHttpClientSSL(); 			break;
			case TWOWAY:
				httpClient = getHttpClientSSLByKeyStore(); 	break;
		}
		CloseableHttpResponse response = null;
		try {
			// POST参数
			if(null != data && !data.isEmpty()){
				StringEntity entity = new StringEntity(data, charset);
				httpPost.setHeader("Content-Type", contentType);
				httpPost.setEntity(entity);
			}

			response = httpClient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				responseBody = entity != null ? EntityUtils.toString(entity, charset) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status + ",apiUrl=" + apiUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseBody;
	}
	
	/**
	 * @Description: TODO(POST，参数是Map)
	 * @param apiUrl URL地址
	 * @param sslType HttpSSLType
	 * @param params POST带的参数
	 * @return String  返回String,
	 */
	public String post(String apiUrl, HttpSSLType sslType, SortedMap<String, String> params) {
		String responseBody = "";
		HttpPost httpPost = new HttpPost(apiUrl);

		CloseableHttpClient httpClient = null;
		switch (sslType) {
			case NONE:
				httpClient = getHttpClient(); 				break;
			case ONEWAY:
				httpClient = getHttpClientSSL(); 			break;
			case TWOWAY:
				httpClient = getHttpClientSSLByKeyStore(); 	break;
		}
		CloseableHttpResponse response = null;
		try {
			// POST参数    
			if(null != params && !params.isEmpty()){
				List<NameValuePair> nameValuePairs = HttpUtil.getNameValuePairs(params);
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, defaultCharset);  
				httpPost.setEntity(entity); 
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			}
			
			response = httpClient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				responseBody = entity != null ? EntityUtils.toString(entity, defaultCharset) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status + ",apiUrl=" + apiUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseBody;
	}
	
	/**
	 * @Title: get
	 * @Description: TODO(GET请求)
	 * @param apiUrl
	 * @param sslType HttpSSLType
	 * @return String 返回类型
	 */
	public String get(String apiUrl,  HttpSSLType sslType) {
		String responseBody = "";
		HttpGet httpGet = new HttpGet(apiUrl);
		CloseableHttpClient httpClient = null;
		switch (sslType) {
			case NONE:
				httpClient = getHttpClient(); 				break;
			case ONEWAY:
				httpClient = getHttpClientSSL(); 			break;
			case TWOWAY:
				httpClient = getHttpClientSSLByKeyStore(); 	break;
		}
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				responseBody = entity != null ? EntityUtils.toString(entity, defaultCharset) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status + ",apiUrl=" + apiUrl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseBody;
	}

	public String getKeyStoreFilePath() {
		return keyStoreFilePath;
	}

	public void setKeyStoreFilePath(String keyStoreFilePath) {
		this.keyStoreFilePath = keyStoreFilePath;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
}
