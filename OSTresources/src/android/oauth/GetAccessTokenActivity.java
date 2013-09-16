package android.oauth;

import java.util.HashMap;
import java.util.Map;

import resources.OAuthConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * This activity aims to retrieve the OAuth Access Token
 * 
 * Execute the OAuthAccessTokenTask to request it.
 * 
 * After the request is sent, a callback is made here in to get it.
 * 
 */
public class GetAccessTokenActivity extends Activity {

	final String TAG = getClass().getName();
	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	    System.out.println("GetAccessTokenActivty starting...");

		new OAuthAccessTokenTask(this).execute();

	}

	/**
	 * Called when the OAuthAccessTokenTask finishes (user has authorized the
	 * request token). The callback URL will be intercepted here.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		final Uri uri = intent.getData();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (uri != null
				&& uri.getScheme().equals(OAuthConstants.OAUTH_CALLBACK_SCHEME)) {

			String query = uri.toString();
			Log.i(TAG, "Callback received : " + query);
			String new_query = query.replace("x-oauthflow://callback#", "");
			System.out.println("new query:" + new_query);

			String[] params = new_query.split("&");
			Map<String, String> map = new HashMap<String, String>();
			for (String param : params) {
				String name = param.split("=")[0];
				String value = param.split("=")[1];
				map.put(name, value);
			}

			String access_token = map.get("access_token");

			Intent returnIntent = new Intent();
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			final Editor edit = prefs.edit();
			edit.putString("access_token", access_token);
			edit.commit();

			returnIntent.putExtra("access_token", access_token);
			finish();
		}
	}
}
