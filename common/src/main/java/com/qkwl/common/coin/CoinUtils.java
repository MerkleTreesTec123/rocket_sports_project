package com.qkwl.common.coin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.*;
import java.net.Authenticator;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoinUtils {

	private static final Logger logger =  LoggerFactory.getLogger(CoinUtils.class);

	// 用户名
	private String accessKey = null;
	// 密码
	private String secretKey = null;
	// 钱包IP地址
	private String ip = null;
	// 端口
	private String port = null;
	// 加密
	private String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	// Wei
	private static final String ETHWEI = "1000000000000000000";
	public static final String ETHWEI_8 = "100000000";

	private OkHttpClient okHttpClient =
			new OkHttpClient.Builder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.writeTimeout(60,TimeUnit.SECONDS)
					.readTimeout(60,TimeUnit.SECONDS)
					.build();

	public static void main(String args[]) {
//		CoinUtils coinUtils = new CoinUtils("XMR","81jRL1GM0Ow69J2MdTKb","127.0.0.1","9090");
//		HashMap<String,String> params = new HashMap<>();
//		params.put("id","8dd43b689ca5d6cd1dc10c3282827fcbdd13b80047314afc3c05916c088cb311");
//        JSONObject jsonObject = coinUtils.goEOS(params, "/v1/history/get_transaction");
//        System.out.println(jsonObject.toJSONString());

		BigDecimal bigDecimal = new BigDecimal("1.00019");
		System.out.println(  bigDecimal.setScale(4,BigDecimal.ROUND_UP).toString());

	}

	public CoinUtils(String accessKey,String secretKey, String ip, String port) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.ip = ip;
		this.port = port;
	}

	public CoinUtils(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

	public JSONObject go(String method, String condition) {
		try {
			String result = "";
			String tonce = "" + (System.currentTimeMillis() * 1000);
			authenticator();

			String params = "tonce=" + tonce.toString() + "&accesskey=" + accessKey + "&requestmethod=post&id=1&method=" + method + "&params=" + condition;

			String hash = getSignature(params, secretKey);

			String url = "http://" + accessKey + ":" + secretKey + "@" + ip + ":" + port;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			String userpass = accessKey + ":" + hash;
			String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
			con.setRequestProperty("Authorization", basicAuth);

			String postdata = "{\"method\":\"" + method + "\", \"params\":" + condition + ", \"id\": 1}";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();

			if (responseCode != 200) {
				logger.error("Coin go failed : {}_{}", responseCode,  result);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}

			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin go error {}，{} ", method, condition, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}

	public JSONObject goETC(String method, String params) {
		try {
			String result = "";
			String url = "http://" + ip + ":" + port;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add reuqest header
			con.addRequestProperty("Content-Type","application/json");
			con.setRequestMethod("POST");
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1}";

			//System.out.println("postdata:"+postdata);

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();
			//System.out.println("result:"+result);
			if (responseCode != 200) {
				logger.error("Coin goETC failed : {}_{}", responseCode,  result);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}

			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goETC error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}

	public JSONObject goETP(String method, String params) {
		try {
			String result = "";
			String url = "http://" + ip + ":" + port + "/rpc";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + "}";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			result = response.toString();

			if (responseCode != 200) {
				logger.error("Coin goETP failed : {}_{}", responseCode,  result);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}

			if (method.equals("getnewaddress") || method.equals("fetch-height")) {
				return JSON.parseObject("{\"result\":\"" + result + "\"}");
			}

			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goETP error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}



	public JSONObject goGXS(String method, String params) {
		try {
			String result = "";
			String url = "http://" + ip + ":" + port + "/rpc";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			String postdata = "{\"method\":\"" + method + "\", \"params\":" + params + ", \"id\": 1}";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			inputLine = in.readLine();
			response.append(inputLine);
			in.close();
			result = response.toString();

			if (responseCode != 200) {
				logger.error("Coin goGXS failed : {}_{}", responseCode,  result);
				return JSON.parseObject("{\"result\":\"null\",\"error\":" + responseCode + ",\"id\":2}");
			}

			if (method.equals("getnewaddress") || method.equals("fetch-height")) {
				return JSON.parseObject("{\"result\":\"" + result + "\"}");
			}

			return JSON.parseObject(result);
		} catch (Exception e) {
			logger.error("Coin goGXS error {}，{} ", method, params, e);
			return JSON.parseObject("{\"result\":\"null\",\"error\":null,\"id\":3}");
		}
	}

	public JSONObject goACT(JSONArray params,String method,String id) {
		JSONObject rpcData = new JSONObject();
		rpcData.put("jsonrpc","2.0");
		rpcData.put("params",params);
		rpcData.put("id",id);
		rpcData.put("method",method);
		sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
		String rpcAuth = "000000"+baseEncoder.encode((accessKey+":"+secretKey).getBytes());;
		try {
			RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),rpcData.toJSONString());
			Request.Builder builder  = new Request.Builder().url("http://"+ip+":"+port+"/rpc")
					.post(requestBody)
					.addHeader("Authorization",rpcAuth);
			Response execute = okHttpClient.newCall(builder.build()).execute();
			String res = execute.body().string();
			return JSON.parseObject(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

	public JSONObject goXMR(JSONObject params,String method) {
		JSONObject rpcData = new JSONObject();
		rpcData.put("jsonrpc","2.0");
		rpcData.put("params",params);
		rpcData.put("id","0");
		rpcData.put("method",method);
		try {
			CredentialsProvider creds = new BasicCredentialsProvider();
			URI rpcUri = new URI("http", null, ip, Integer.parseInt(port), "/json_rpc", null, null);
			creds.setCredentials(new AuthScope(rpcUri.getHost(), rpcUri.getPort()), new UsernamePasswordCredentials(accessKey, secretKey));
			HttpClient client = HttpClients.custom().setDefaultCredentialsProvider(creds).build();
			HttpEntity entity = new StringEntity(rpcData.toString());
			HttpPost post = new HttpPost(rpcUri);
			post.setEntity(entity);
			HttpResponse resp = client.execute(post);
			String responseContent = StreamUtils.copyToString(resp.getEntity().getContent(), Charset.forName("utf-8"));
			return JSONObject.parseObject(responseContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

	public JSONObject goBTM(JSONObject params,String method) {
		try {
			RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),params.toJSONString());
			Request.Builder builder  = new Request.Builder().url("http://"+ip+":"+port+"/"+method)
					.post(requestBody);
			Response execute = okHttpClient.newCall(builder.build()).execute();
			String res = execute.body().string();
			return JSON.parseObject(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

	public JSONObject goEOS(HashMap<String,String> params, String method) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("?");
			Iterator<String> iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				stringBuilder.append(key);
				stringBuilder.append("=");
				stringBuilder.append(params.get(key));
				stringBuilder.append("&");
			}
			if (stringBuilder.length() >= 2) {
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			}
			String url = "http://"+ip+":"+port+""+method+stringBuilder.toString();
			Request.Builder builder  = new Request.Builder().url(url).get();
			Response execute = okHttpClient.newCall(builder.build()).execute();
			return JSON.parseObject(execute.body().string());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

	private String getSignature(String data, String key) throws Exception {
		// get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);

		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(data.getBytes());
		return bytArrayToHex(rawHmac);
	}

	private String bytArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for (byte b : a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}

	// The easiest way to tell Java to use HTTP Basic authentication is to set a default Authenticator:
	private void authenticator() {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(accessKey, secretKey.toCharArray());
			}
		});
	}

	/**
	 * 大数:16进制转string
	 * @return
	 */
	public static String BigHexToString(String bigHex) {
		BigInteger bigInteger = new BigInteger(bigHex.substring(2), 16);
		return bigInteger.toString();
	}

	/**
	 * ETH余额:16进制 换算为10进制2位小数
	 * @param hexBalance
	 * @return
	 */
	public static String ETHBalanceHexToStr(String hexBalance) {
		BigInteger hexBalanceTmp = new BigInteger(hexBalance.substring(2), 16);
		BigDecimal balance = new BigDecimal(hexBalanceTmp.toString());
		BigDecimal balanceWei = new BigDecimal(ETHWEI);
		BigDecimal result = balance.divide(balanceWei,4,RoundingMode.DOWN);
		return result.toString();
	}

	/**
	 * ETH余额:16进制 换算为10进制2位小数
	 * @param hexBalance
	 * @return
	 */
	public static String ETHBalanceHexToStr(String hexBalance, int wei) {
		BigInteger hexBalanceTmp = new BigInteger(hexBalance.substring(2), 16);
		BigDecimal balance = new BigDecimal(hexBalanceTmp.toString());
		BigDecimal balanceWei = new BigDecimal(Math.pow(10L, wei));
		BigDecimal result = balance.divide(balanceWei,4,RoundingMode.DOWN);
		return result.toString();
	}

	/**
	 * ETH余额:10进制2位小数换算位16进制
	 * @param Balance
	 * @return
	 */
	public static String ToETHBalanceHex(String Balance) {
		BigDecimal balance = new BigDecimal(Balance);
		BigDecimal balanceWei = new BigDecimal(ETHWEI);
		BigInteger hexBalanceTmp = balance.multiply(balanceWei).toBigInteger();
		String resultStr = "0x" + hexBalanceTmp.toString(16);
		return resultStr;
	}

	/**
	 * ETH余额:10进制2位小数换算位16进制
	 * @param Balance
	 * @return
	 */
	public static String CntractToETHBalanceHex(String Balance, int wei) {
		BigDecimal balance = new BigDecimal(Balance);
		BigInteger hexBalanceTmp;
		if (wei == 0) {
			hexBalanceTmp = balance.toBigInteger();
		} else {
			BigDecimal balanceWei = new BigDecimal(Math.pow(10L, wei));
			hexBalanceTmp = balance.multiply(balanceWei).toBigInteger();
		}
		String resultStr = StringUtils.leftPad(hexBalanceTmp.toString(16), 64, "0");
		return resultStr;
	}

	public static BigDecimal compatPrecisionWithEOS(BigDecimal bigDecimal) {
		return bigDecimal.setScale(4,BigDecimal.ROUND_UP);
	}

	private static final TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[]{};
				}
			}
	};
	private static final SSLContext trustAllSslContext;
	static {
		try {
			trustAllSslContext = SSLContext.getInstance("SSL");
			trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

	public static OkHttpClient trustAllSslClient(OkHttpClient client) {
		OkHttpClient.Builder builder = client.newBuilder();
		builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager)trustAllCerts[0]);
		builder.hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		return builder.build();
	}

	public String getHttp(String url) {
		Request.Builder builder  = new Request.Builder().url(url).get();
		try {
			Response execute = trustAllSslClient(okHttpClient).newCall(builder.build()).execute();
			return execute.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String postHttp() {
		return "";
	}


}
