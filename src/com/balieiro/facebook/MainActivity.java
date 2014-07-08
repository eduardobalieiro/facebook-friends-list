/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <balieiro@kajoo.com.br> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */

package com.balieiro.facebook;

import java.io.File;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	
	private int mActiveFragment;
	private Session.StatusCallback mStatusCallback = new SessionStatusCallback();

	@Override
	public void onResume() {
		Log.d("MainActivity: ", "onResume()");
		Session.getActiveSession().addCallback(mStatusCallback);
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		switch (item.getItemId()){ 
		case R.id.action_clear_cache:
			final Thread thread = new Thread() {
		        @Override
		        public void run() {
		        	clearPictureCache(getFilesDir());
		        }
		    };
		    thread.start();
			
			return true;
		case R.id.action_logout:
			logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
     * This method will delete recursively all files within the specified 
     * file path.
     * @param  filePath The top directory to be cleaned
     */
	private static void clearPictureCache(File filePath) {
	    if (filePath.isDirectory())
	        for (File child : filePath.listFiles())
	        	clearPictureCache(child);
	    filePath.delete();
	}
	
	private void logout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed())
			session.closeAndClearTokenInformation();
		
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, mStatusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(mStatusCallback));
			}
		}
		if (session.isOpened()) {
			setContentView(R.layout.friends_list_launcher);
			getActionBar().show();
			mActiveFragment = R.layout.friends_list_fragment;
		} else {
			setContentView(R.layout.welcome_launcher);
			getActionBar().hide();
			mActiveFragment = R.layout.welcome_fragment;
		}

	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			updateUI();
		}
	}
	private void updateUI(){
		Session session = Session.getActiveSession();
		if ((session.isOpened() && mActiveFragment == R.layout.welcome_fragment) ||
				(session.isClosed() && mActiveFragment == R.layout.friends_list_fragment)){
			// Restart this activity in case the session state changed and is final
			Log.d("MainActivity: ", "SessionStatus updated");
			recreate();
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession()
		.onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Session.getActiveSession().removeCallback(mStatusCallback);
	}

}
