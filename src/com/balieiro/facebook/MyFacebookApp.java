/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 423):
 * <balieiro@kajoo.com.br> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.
 * ----------------------------------------------------------------------------
 */

package com.balieiro.facebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.Response;
import com.facebook.model.GraphObject;

import android.app.Application;
import android.content.Context;

public class MyFacebookApp extends Application {

	private ArrayList<FriendItem> mOrderedFriendList;
	
    private static MyFacebookApp instance;

    @Override
    public void onCreate (){
    	instance = this;
    	mOrderedFriendList = new ArrayList<FriendItem>();
    }
    
    public static Context getContext() {
    	return instance;
    }

	public static boolean isFriendListEmpty() {
		return instance.mOrderedFriendList.size() == 0;
	}

	public static void loadFriendsList(Response response) {
		try{
            GraphObject graphObject = response.getGraphObject();
            JSONObject jsonObject = graphObject.getInnerJSONObject();
            JSONArray array = jsonObject.getJSONArray("data");
            instance.mOrderedFriendList.clear();
            for(int i=0; i < array.length(); i++){
                JSONObject friend = array.getJSONObject(i);
                FriendItem friendItem = FriendItem.getInstance(friend);
                if (friendItem != null){
                	instance.mOrderedFriendList.add(friendItem);
                }
            }
            Collections.sort(instance.mOrderedFriendList, instance.new FriendComparator());

        } catch(Exception e){
            e.printStackTrace();
        }
		
	}

	public static ArrayList<FriendItem> getFriendsList() {
		return instance.mOrderedFriendList;
	}
	private class FriendComparator implements Comparator<FriendItem>{
		
		public FriendComparator(){}
		
		@Override
		public int compare(FriendItem p1, FriendItem p2) {
			return p1.getName().compareTo(p2.getName());
		}
	}

}

