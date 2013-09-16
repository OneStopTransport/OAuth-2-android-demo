package demo.project;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import resources.OAuthConstants;
import resources.UserMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.oauth.GetAccessTokenActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * This example aims to demonstrate how to interact with OST OAuth server
 * and how to use the provided resources to simplify the process
 *
 */
public class MainActivity extends Activity {
	
	//initializations
	private TextView loggedUser;
	private SharedPreferences prefs;
	private Button checkButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		loggedUser = (TextView) findViewById(R.id.logged_user);
		checkButton = (Button) findViewById(R.id.button_response);
		

		//Making sure developers have followed the instructions
		if(OAuthConstants.CONSUMER_KEY.compareTo("YOUR_CONSUMER_KEY")==0||OAuthConstants.CONSUMER_SECRET.compareTo("YOUR_CONSUMER_SECRET")==0){
			loggedUser.setText("Please set your OAuth consumer variables in OSTresources/src/resources/OAuthConstants.java");
		}
		else{
			
			Intent intent = new Intent(this, GetAccessTokenActivity.class);
			startActivity(intent);
			

			//getting the OAuth Access Token
			String access_token = prefs.getString("access_token", "");

			if(access_token.compareTo("")!=0){
				new GetUserInfoTask().execute(access_token);

			}else{			
				loggedUser.setText("Error retrieving access_token");
			}

		}


	
	}
	
	
	//Handler for the button that allows the user to see the complete json response
	public void onResponseButtonClicked(View v) {

		AlertDialog.Builder userAlert = new AlertDialog.Builder(this);

		String usersResponse = prefs.getString("users.json", "");
		userAlert.setMessage(usersResponse)
				 .setTitle("Complete response:")
				 .setCancelable(false)
				 .setNegativeButton("Dismiss",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		userAlert.show();

	}

	/* After the access token is retrieved, it's used get user data
	 * The access token is being passed as a parameter to an ASyncTask
	 * that is handling the network calls. Check GetUserInfoTask below
	 */
	@Override
	public void onResume() {
		super.onResume(); 
	

	}

	/**
	 * An asynchronous task that communicates with OST to retrieve user
	 * information.
	 * 
	 * @param String Access_Token
	 *               OAuth Access Token
	 * 
	 */
	public class GetUserInfoTask extends AsyncTask<String, Void, Void> {

		final String TAG = getClass().getName();
		private String username;
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Void doInBackground(String... params) {

			System.out.println("IOMG");
			// OAuth access token was passed as a task parameter
			String access_token = params[0];

			try {
				
				
				/*
				 * The full OST API can be found here: 
				 * https://developer.ost.pt/api-explorer/
				 */
				
				String url = "https://api.ost.pt/users?bearer_token="+ access_token;

				OAuthRequest request = new OAuthRequest(Verb.GET, url);
				Response response = request.send();

				//parsing username
				Gson gson = new Gson();
				UserMessage userMessage = gson.fromJson(response.getBody(),UserMessage.class);
				username = userMessage.getUser().getUsername();

			} catch (Exception e) {

				Log.e(TAG, "Error retrieving user information", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//setting the username in the UI
			checkButton.setEnabled(true);
			loggedUser.setText(username);

		}

	}

}
