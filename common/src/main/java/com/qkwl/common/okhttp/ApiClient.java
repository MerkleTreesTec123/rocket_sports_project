package com.qkwl.common.okhttp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.qkwl.common.huobi.request.*;
import com.qkwl.common.huobi.response.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qkwl.common.okhttp.ApiClient.toQueryString;

/**
 * http client
 */
public class ApiClient {

  static final int CONN_TIMEOUT = 5;
  static final int READ_TIMEOUT = 5;
  static final int WRITE_TIMEOUT = 5;

  static final MediaType JSON = MediaType.parse("application/json");

  static final OkHttpClient client = getUnsafeOkHttpClient().newBuilder()
          .connectTimeout(10, TimeUnit.SECONDS)
          .readTimeout(10, TimeUnit.SECONDS)
          .writeTimeout(10, TimeUnit.SECONDS)
          .build();

  final String accessKeyId;
  final String accessKeySecret;
  final String assetPassword;

  public static void main(String args[]){
    ApiSignature apiSignature = new ApiSignature();
    String appKey = "e2b12a8d37d044b8a59ec13e828e9e23";
    String appSecret = "9149E3B95ADC8C4E9BFB6DECC4D07631";
    Map<String,String> params = new HashMap<>();
    apiSignature.createSignature(appKey,appSecret,"GET","www.ccnex.com","/v1/balance.html",params);

    try {
      Response execute = client.newCall(new Request.Builder().url("https://www.ccnex.com/v1/balance.html" + "?" + toQueryString(params)).get().build()).execute();
      System.out.printf(execute.body().string());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * 创建一个ApiClient实例
   * 
   * @param accessKeyId AccessKeyId
   * @param accessKeySecret AccessKeySecret
   */
  public ApiClient(String accessKeyId, String accessKeySecret) {
    this.accessKeyId = accessKeyId;
    this.accessKeySecret = accessKeySecret;
    this.assetPassword = null;
  }

  /**
   * 创建一个ApiClient实例
   * 
   * @param accessKeyId AccessKeyId
   * @param accessKeySecret AccessKeySecret
   * @param assetPassword AssetPassword
   */
  public ApiClient(String accessKeyId, String accessKeySecret, String assetPassword) {
    this.accessKeyId = accessKeyId;
    this.accessKeySecret = accessKeySecret;
    this.assetPassword = assetPassword;
  }

  String authData() {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    md.update(this.assetPassword.getBytes(StandardCharsets.UTF_8));
    md.update("hello, moto".getBytes(StandardCharsets.UTF_8));
    Map<String, String> map = new HashMap<>();
    map.put("assetPwd", DatatypeConverter.printHexBinary(md.digest()).toLowerCase());
    try {
      return ApiSignature.urlEncode(JsonUtil.writeValue(map));
    } catch (IOException e) {
      throw new RuntimeException("Get json failed: " + e.getMessage());
    }
  }

  // Encode as "a=1&b=%20&c=&d=AAA"
  static String toQueryString(Map<String, String> params) {
    return String.join("&", params.entrySet().stream().map((entry) -> {
      return entry.getKey() + "=" + ApiSignature.urlEncode(entry.getValue());
    }).collect(Collectors.toList()));
  }

  private static OkHttpClient getUnsafeOkHttpClient() {
    try {
      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustAllCerts = new TrustManager[] {
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

      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.sslSocketFactory(sslSocketFactory);
      builder.hostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      });

      OkHttpClient okHttpClient = builder.build();
      return okHttpClient;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



}

class ApiSignature {

  final Logger log = LoggerFactory.getLogger(getClass());

  static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
  static final ZoneId ZONE_GMT = ZoneId.of("Z");

  /**
   * 创建一个有效的签名。该方法为客户端调用，将在传入的params中添加AccessKeyId、Timestamp、SignatureVersion、SignatureMethod、Signature参数。
   * 
   * @param appKey AppKeyId.
   * @param appSecretKey AppKeySecret.
   * @param method 请求方法，"GET"或"POST"
   * @param host 请求域名，例如"be.huobi.com"
   * @param uri 请求路径，注意不含?以及后的参数，例如"/v1/huobi/info"
   * @param params 原始请求参数，以Key-Value存储，注意Value不要编码
   */
  public void createSignature(String appKey, String appSecretKey, String method, String host, String uri, Map<String, String> params) {
    StringBuilder sb = new StringBuilder(1024);
    sb.append(method.toUpperCase()).append('\n') // GET
        .append(host.toLowerCase()).append('\n') // Host
        .append(uri).append('\n'); // /path
    params.remove("Signature");
    params.put("AccessKeyId", appKey);
    params.put("SignatureVersion", "2");
    params.put("SignatureMethod", "HmacSHA256");
    params.put("Timestamp", String.valueOf(System.currentTimeMillis()/1000));
    // build signature:
    SortedMap<String, String> map = new TreeMap<>(params);
    for (Map.Entry<String, String> entry : map.entrySet()) {
      if (entry.getKey().equals("token"))continue;
      String key = entry.getKey();
      String value = entry.getValue();
      sb.append(key).append('=').append(urlEncode(value)).append('&');
    }
    // remove last '&':
    sb.deleteCharAt(sb.length() - 1);
    // sign:
    Mac hmacSha256 = null;
    try {
      hmacSha256 = Mac.getInstance("HmacSHA256");
      SecretKeySpec secKey =
          new SecretKeySpec(appSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      hmacSha256.init(secKey);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("No such algorithm: " + e.getMessage());
    } catch (InvalidKeyException e) {
      throw new RuntimeException("Invalid key: " + e.getMessage());
    }
    String payload = sb.toString();
    byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
    String actualSign = Base64.getEncoder().encodeToString(hash);
    params.put("Signature", actualSign);
    if (log.isDebugEnabled()) {
      log.debug("Dump parameters:");
      for (Map.Entry<String, String> entry : params.entrySet()) {
        log.debug("  key: " + entry.getKey() + ", value: " + entry.getValue());
      }
    }
  }

  /**
   * 使用标准URL Encode编码。注意和JDK默认的不同，空格被编码为%20而不是+。
   * 
   * @param s String字符串
   * @return URL编码后的字符串
   */
  public static String urlEncode(String s) {
    try {
      return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("UTF-8 encoding not supported!");
    }
  }

  /**
   * Return epoch seconds
   */
  long epochNow() {
    return Instant.now().getEpochSecond();
  }

  String gmtNow() {
    return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT).format(DT_FORMAT);
  }
}


class JsonUtil {

  public static String writeValue(Object obj) throws IOException {
    return objectMapper.writeValueAsString(obj);
  }

  public static <T> T readValue(String s, TypeReference<T> ref) throws IOException {
    return objectMapper.readValue(s, ref);
  }

  static final ObjectMapper objectMapper = createObjectMapper();

  static ObjectMapper createObjectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }



}
