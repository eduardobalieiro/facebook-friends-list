package com.balieiro.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class FriendsListFragment extends Fragment {
	
	private static final int BATCH_SIZE = 20;
	private FriendsListAdapter mAdapter = new FriendsListAdapter(this);

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.friends_list_fragment, container, false);
		ListView list = (ListView) view.findViewById(R.id.list_view);
		list.setAdapter(mAdapter);

		
        list.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Load more items in the bottom of this list in case user is 
				// reaching close to the end.
				if (totalItemCount - (firstVisibleItem + visibleItemCount) < 5 && 
						totalItemCount > 0){
					loadItems();
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
        return view;
    }
	/**
     * It seems FACEBOOK is no longer supporting the download of friends_list unless
     * users are registered to your app. Check this link:
     * https://developers.facebook.com/bugs/1502515636638396
     * The temporary solution is to enable the user as part of the test team
     * in order to retrieve the list.
     */
	private void updateFriendsList(){

		// Useless in this method. Left here for documentation
		String fqlQuery = "SELECT uid, name FROM user WHERE uid IN (SELECT uid2 FROM " + 
							"friend WHERE uid1 = me()) ORDER BY first_name ASC";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		Request request = new Request(session, "/me/taggable_friends", params, null, 
				mFriendListCallback); 
		Request.executeBatchAsync(request);       
	}
	
	private Request.Callback mFriendListCallback =  new Request.Callback(){         
		public void onCompleted(Response response) {
			MyFacebookApp.loadFriendsList(response);
			mAdapter.removeAll();
			loadItems();
		}
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
	private void loadItems() {
		int i = 0;
		for (int x = mAdapter.getCount(); x < MyFacebookApp.getFriendsList().size(); x++){
			mAdapter.addItem(MyFacebookApp.getFriendsList().get(x));
			if (i++ == BATCH_SIZE)
				break;
		}
		mAdapter.notifyDataSetChanged();
	}
	@Override
	public void onPause(){
		super.onPause();
		mAdapter.removeAll();
	}
	@Override
	public void onResume(){
		super.onPause();
		// Verify if friends list is empty, then update it. 
		if (MyFacebookApp.isFriendListEmpty()){
			updateFriendsList();	
		} else {
			loadItems();
		}
	}

}
