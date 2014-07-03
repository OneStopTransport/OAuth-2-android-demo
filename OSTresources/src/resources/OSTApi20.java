package resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

public class OSTApi20 extends DefaultApi20{

	
	 private static final String DEFAULT_METHOD = "https";
	 private static final String DEFAULT_PORT = "80";
	 private static final String DEFAULT_HOST = "auth.ost.pt";
	 private static final String AUTHORIZE_URL = "https://auth.ost.pt/v2/authorize/";

	 private static String host;
	 private static String port;
	 private static String method;

	 
	 @Override
	 public String getAccessTokenEndpoint()
	 {
	    setHostname();
	    return String.format("%s://%s/v2/access_token/", method, host);
	 }
	 
	 @Override
	 public Verb getAccessTokenVerb()
	 {
	    return Verb.POST;
	 }
	
	 @Override
	 public AccessTokenExtractor getAccessTokenExtractor()
	 {
	    return new JsonTokenExtractor();
	 }
	 
	 @Override
	 public String getAuthorizationUrl(OAuthConfig config)
	 {
		 
	    setHostname();
	    System.setProperty("https.protocols", "SSLv3");
	    StringBuilder authUrl = new StringBuilder();
	    authUrl.append(String.format(AUTHORIZE_URL, method, host, port));
	 
	    authUrl.append("?scope=").append(OAuthEncoder.encode(config.getScope())); 
	    authUrl.append("&response_type=token");
	    authUrl.append("&redirect_uri=").append(OAuthEncoder.encode(config.getCallback()));
	    authUrl.append("&client_id=").append(OAuthEncoder.encode(config.getApiKey()));
	    return authUrl.toString();
	 }
	 
	 
	 /*
	   * sets the host, port, and method from a properties file the first time this method is run.
	   */
	 private void setHostname()
	 {
	    if (null == host || null == port || null == method)
	    {
	      Properties prop = OSTApi20.loadProperties();
	      host = prop.getProperty("oauth2.authz.hostname", OSTApi20.DEFAULT_HOST);
	      port = prop.getProperty("oauth2.authz.port", OSTApi20.DEFAULT_PORT);
	      method = prop.getProperty("oauth2.authz.method", OSTApi20.DEFAULT_METHOD);
	    }
	 }
	
	 
	 protected static Properties loadProperties()
     {
	    final Properties prop = new Properties();
	    try
	    {
	      final InputStream propertiesStream = OSTApi20.class.getResourceAsStream("/ost.properties");
	      if (propertiesStream != null)
	        prop.load(propertiesStream);
	    }
	    catch (IOException e)
	    {
	      throw new OAuthException("Error while reading properties file", e);
	    }
	    return prop;
	 }
}
