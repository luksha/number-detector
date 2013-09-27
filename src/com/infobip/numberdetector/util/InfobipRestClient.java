package com.infobip.numberdetector.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.infobip.push.lib.util.Consts;
import com.infobip.push.lib.util.Util;

/**
* Class contains methods for sending HTTP GET, POST and PUT request.
* @author tjuric
*
*/
public class InfobipRestClient {
       
       public static final int GET       = Consts.GET;        // 1
       public static final int POST      = Consts.POST;       // 2
       public static final int PUT       = Consts.PUT;        // 3
       
       private JSONObject jsonParams;
       private ArrayList<NameValuePair> headers;

       private String url;

       private int responseCode;
       private String message;
       private String response;

       public String getResponse() {
              return response;
       }

       public String getMessage() {
              return message;
       }

       public int getResponseCode() {
              return responseCode;
       }

       public InfobipRestClient(String url) {
              this.url = url;
              jsonParams = new JSONObject();
              headers = new ArrayList<NameValuePair>();
       }

       public void addParam(String name, String value) {
              try {
                     jsonParams.put(name, value);
              } catch (JSONException e) {
                     Util.LogError(String.format("Invalid parameters: name %s, value %s", name, value), this);
              }
       }

       public void addParams(JSONObject params) {
              jsonParams = params;
       }
       
       public void addHeader(String name, String value) {
              headers.add(new BasicNameValuePair(name, value));
       }

       public void execute(int method) throws Exception {
              
              switch (method) {
                     case GET: {
                            // add parameters
                           String combinedParams = "";
                           combinedParams += "?";
                            @SuppressWarnings("unchecked")
                           Iterator<String> keys = jsonParams.keys();
                            while (keys.hasNext()) {
                                  String key = keys.next();
                                  String paramString = key + "=" + URLEncoder.encode(jsonParams.get(key).toString(), Consts.CHAR_SET);
                                  if (combinedParams.length() > 1) {
                                         combinedParams += "&" + paramString;
                                  } else {
                                         combinedParams += paramString;
                                  }
                           }
       
                           HttpGet request = new HttpGet(url + combinedParams);
       
                            // add headers
                            for (NameValuePair h : headers) {
                                  if (!request.containsHeader(h.getName())) {
                                         request.addHeader(h.getName(), h.getValue());
                                  }
                           }
       
                           executeRequest(request);
                            break;
                     }                    
                     case POST: {
                           String jsonEntity = jsonParams.toString();
                           executePost(jsonEntity);
                            break;
                     }                    
                     case PUT: {                 
                           String jsonEntity = jsonParams.toString();
                           executePut(jsonEntity);                  
                            break;
                     }
              }
       }

       private void executePost(String jsonEntity) throws Exception {
              HttpPost request = new HttpPost(url);
              addHeader("Content-Type", "application/json");
              addHeader("Accept", "application/json");
              
              // add headers
              for (NameValuePair h : headers) {
                     if (!request.containsHeader(h.getName())) {
                           request.addHeader(h.getName(), h.getValue());
                     }
              }

              if (jsonEntity != null) {
                     Util.LogDebug("REST client sending JSON: " + jsonEntity);
                     request.setEntity(new StringEntity(jsonEntity, Consts.CHAR_SET));
              }

              executeRequest(request);
       }
       
       private void executePut(String jsonEntity) throws Exception {
              HttpPut request = new HttpPut(url);
              addHeader("Content-Type", "application/json");
              addHeader("Accept", "application/json");
              
              // add headers
              for (NameValuePair h : headers) {
                     if (!request.containsHeader(h.getName())) {
                           request.addHeader(h.getName(), h.getValue());
                     }
              }
              
              if (jsonEntity != null) {
                     Util.LogDebug("REST client sending JSON: " + jsonEntity);
                     request.setEntity(new StringEntity(jsonEntity, Consts.CHAR_SET));
              }
              
              executeRequest(request);
       }

       private void executeRequest(HttpUriRequest request)    throws Exception {

              DefaultHttpClient client = null;
              try {

                     // final SchemeRegistry schemeRegistry = new SchemeRegistry();
                     // schemeRegistry.register(new Scheme("http",
                     // PlainSocketFactory.getSocketFactory(), 80));
                     // schemeRegistry.register(new Scheme("https",
                     // createAdditionalCertsSSLSocketFactory(), 443));

                     // Set basic data
                     HttpParams params = new BasicHttpParams();
                     HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                     HttpProtocolParams.setContentCharset(params, Consts.CHAR_SET);
                     HttpProtocolParams.setUseExpectContinue(params, true);
                     HttpProtocolParams.setUserAgent(params, Consts.USER_AGENT);

                     // Make pool
                     ConnPerRoute connPerRoute = new ConnPerRouteBean(12);
                     ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
                     ConnManagerParams.setMaxTotalConnections(params, 20);

                     // Set timeout
                     HttpConnectionParams.setStaleCheckingEnabled(params, false);
                     HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
                     HttpConnectionParams.setSoTimeout(params, 60 * 1000);
                     HttpConnectionParams.setSocketBufferSize(params, 8192);

                     // Some client params
                     HttpClientParams.setRedirecting(params, false);

                     // final ClientConnectionManager tscm = new
                     // ThreadSafeClientConnManager(params, schemeRegistry);
                     // client = new DefaultHttpClient(tscm, params);
                     client = new DefaultHttpClient(params);

                     HttpResponse httpResponse = client.execute(request);
                     responseCode = httpResponse.getStatusLine().getStatusCode();
                     message = httpResponse.getStatusLine().getReasonPhrase();

                     HttpEntity entity = httpResponse.getEntity();

                     Util.LogDebug("responseCode: " + String.valueOf(responseCode));             
                     Util.LogDebug("message: " + message);

                     if (entity != null) {
                           InputStream instream = null;
                            try {
                                  instream = entity.getContent();
                                  response = convertStreamToString(instream);
//                                Util.LogDebug(response);
                                  
                           } finally {
                                  // Closing the input stream will trigger connection release
                                  instream.close();
                           }
                     }
                     
              } catch (Exception e) {
                     if (client != null) {
                           client.getConnectionManager().shutdown();
                     }

                     responseCode = -1;
                     message = e.toString();
                     response = e.toString();
                     throw e;
              }
       }

       private static String convertStreamToString(InputStream is) {

              BufferedReader reader = new BufferedReader(new InputStreamReader(is));
              StringBuilder sb = new StringBuilder();

              String line = null;
              try {
                     while ((line = reader.readLine()) != null) {
                           sb.append(line + "\n");
                     }
              } catch (IOException e) {
                     e.printStackTrace();
              } finally {
                     try {
                            is.close();
                     } catch (IOException e) {
                           e.printStackTrace();
                     }
              }
              return sb.toString();
       }
}
