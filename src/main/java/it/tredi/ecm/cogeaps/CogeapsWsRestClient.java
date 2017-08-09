package it.tredi.ecm.cogeaps;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.service.bean.EcmProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CogeapsWsRestClient {

    private String protocol;
    private String host;
    private int port;
    private String carica_service;
    private String stato_elaborazione_service;
    private String username;
    private String password;
    private boolean proxyAttivo;

    public static final Logger LOGGER = Logger.getLogger(CogeapsWsRestClient.class);

    @Autowired private EcmProperties ecmProperties;
    @Autowired private ObjectMapper jacksonObjectMapper;

	public  CogeapsCaricaResponse carica(String reportFileName, byte []xmlReport, String codOrg) throws Exception {
		String complete_url = "";
		if(port != -1)
			complete_url = protocol + "://" + host + ":" + port +  carica_service + "/" + codOrg;
		else
			complete_url = protocol + "://" + host +  carica_service + "/" + codOrg;

		LOGGER.info("Executing cogeaps request: " + complete_url);
		LOGGER.info("proxyAttivo: " + proxyAttivo);

		HttpHost target = new HttpHost(host, port, protocol);
		CloseableHttpClient httpclient = buildHttpClient(target, proxyAttivo);
        try {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpPost httpPost = new HttpPost(complete_url);

            //file allegato
            ByteArrayBody bab = new ByteArrayBody(xmlReport, reportFileName);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bab).build();
            httpPost.setEntity(reqEntity);

            //invio richiesta POST
            CloseableHttpResponse response = httpclient.execute(target, httpPost, localContext);

            try {
            	String response_s = EntityUtils.toString(response.getEntity());
            	LOGGER.info("cogeaps http response code: " + response.getStatusLine());
                LOGGER.info("cogeaps response: " + response_s);

                CogeapsCaricaResponse cogeapsCaricaResponse = jacksonObjectMapper.readValue(response_s, CogeapsCaricaResponse.class);
                cogeapsCaricaResponse.setResponse(response_s);
                cogeapsCaricaResponse.setHttpStatusCode(response.getStatusLine().getStatusCode());
                return cogeapsCaricaResponse;
            }
            finally {
                response.close();
            }
        }
        finally {
            httpclient.close();
        }
	}

	public CogeapsStatoElaborazioneResponse statoElaborazione(String fileName) throws Exception {
		String complete_url = "";

		//dpranteda 09/08/2017
		fileName = URLEncoder.encode(fileName,"UTF-8");

		if(port != -1)
			complete_url = protocol + "://" + host + ":" + port +  stato_elaborazione_service + "?nomeFile=" + fileName;
		else
			complete_url = protocol + "://" + host +  stato_elaborazione_service + "?nomeFile=" + fileName;

		LOGGER.info("Executing cogeaps request: " + complete_url);
		LOGGER.info("proxyAttivo: " + proxyAttivo);

		HttpHost target = new HttpHost(host, port, protocol);
		CloseableHttpClient httpclient = buildHttpClient(target, proxyAttivo);
        try {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpGet httpGet = new HttpGet(complete_url);

            //invio richiesta GET
            CloseableHttpResponse response = httpclient.execute(target, httpGet, localContext);

            try {
            	String response_s = EntityUtils.toString(response.getEntity());
            	LOGGER.info("cogeaps http response code: " + response.getStatusLine());
                LOGGER.info("cogeaps response: " + response_s);

                CogeapsStatoElaborazioneResponse cogeapsStatoElaborazioneResponse = jacksonObjectMapper.readValue(response_s, CogeapsStatoElaborazioneResponse.class);
                cogeapsStatoElaborazioneResponse.setResponse(response_s);
                cogeapsStatoElaborazioneResponse.setHttpStatusCode(response.getStatusLine().getStatusCode());
                return cogeapsStatoElaborazioneResponse;
            }
            finally {
                response.close();
            }
        }
        finally {
            httpclient.close();
        }
	}

	private CloseableHttpClient buildHttpClient(HttpHost target, boolean proxyEnabled){
		/* AUTENTICAZIONE COGEAPS */
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), new UsernamePasswordCredentials(username, password));

        /* HTTP CLIENT */
        CloseableHttpClient httpclient = null;

        if(proxyEnabled){
			/* SETTO PROXY */
        	HttpHost proxy = new HttpHost(ecmProperties.getProxyHost(), ecmProperties.getProxyPort(),ecmProperties.getProxyProtocol());
	        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
	        if(ecmProperties.isProxyAuthenticated()){
	        	/* PROXY NECESSITA AUTHENTICAZIONE */
	        	credsProvider.setCredentials(new AuthScope(proxy.getHostName(), proxy.getPort()), new UsernamePasswordCredentials(ecmProperties.getProxyUsername(), ecmProperties.getProxyPassword()));
	        }
	       return httpclient = HttpClients.custom()
	        		.setRoutePlanner(routePlanner)
	        		.setDefaultCredentialsProvider(credsProvider)
	        		.build();
		}else{
			return httpclient = HttpClients.custom()
	        		.setDefaultCredentialsProvider(credsProvider)
	        		.build();
		}
	}

	public String testProxy(boolean withProxy, boolean withProxyAuthentication) throws Exception {
		String complete_url = "";
		if(port != -1)
			complete_url = protocol + "://" + host + ":" + port;
		else
			complete_url = protocol + "://" + host;

		LOGGER.info("Executing cogeaps request: " + complete_url);
		LOGGER.info("proxyAttivo: " + withProxy);
		LOGGER.info("withProxyAuthentication: " + withProxyAuthentication);

		HttpHost target = new HttpHost(host, port, protocol);
		CloseableHttpClient httpclient = buildHttpClient(target, withProxy);

        try {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpGet httpGet = new HttpGet(complete_url);

            //invio richiesta GET
            CloseableHttpResponse response = httpclient.execute(target, httpGet, localContext);

            try {
            	String response_s = EntityUtils.toString(response.getEntity());
            	LOGGER.info("cogeaps http response code: " + response.getStatusLine());
                LOGGER.info("cogeaps response: " + response_s);

                return response_s;
            }
            finally {
                response.close();
            }
        }
        finally {
            httpclient.close();
        }
	}

}

