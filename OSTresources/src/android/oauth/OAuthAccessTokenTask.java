package android.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import resources.OAuthConstants;
import resources.OSTApi20;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * An asynchronous task that communicates with OST to retrieve
 * the OAuth Access Token
 * 
 */
public class OAuthAccessTokenTask extends AsyncTask<Void, Void, Void> {

	final String TAG = getClass().getName();
	private Context	context;
	OAuthService service;	
	private static final Token EMPTY_TOKEN = null;

	/**
	 * 
	 * @param 	context
	 * 			Required to be able to start the intent to launch the browser.
	 * 		
	 */
	public OAuthAccessTokenTask(Context context) {
		
		this.context = context;
		
	}
	/**
	 * 
	 * Retrieve the OAuth Authorize Token and present a browser to the user to authorize the token.
	 * 
	 */
	protected Void doInBackground(Void... params) {

			try {
				System.setProperty("debug", "true");
	    		service = new ServiceBuilder()
	    		.provider(OSTApi20.class)
	            .apiKey(OAuthConstants.CONSUMER_KEY)
	            .apiSecret(OAuthConstants.CONSUMER_SECRET)
	            .callback(OAuthConstants.OAUTH_CALLBACK_URL)
	            .scope(OAuthConstants.SCOPE)
	            .debug()
	            .build();

	    	    System.out.println("Fetching authorization url...");
  
	    	    String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
	    	    
	    	    System.out.println("Got the Authorization URL!:"+authorizationUrl);
	    	    System.setProperty("https.protocols", "SSLv3");
				Intent intent = new Intent(context,WebViewActivity.class);
				Log.i(TAG, "Popping a browser with the authorize URL : " + authorizationUrl);
				intent.putExtra("url",authorizationUrl);
				context.startActivity(intent);
				
			
		} catch (Exception e) {
			
			Log.e(TAG, "Error during OAUth retrieve request token", e);
		}
			
		return null;
	}

}