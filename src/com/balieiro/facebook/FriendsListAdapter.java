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
import java.util.List;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendsListAdapter extends BaseAdapter {
	private List<FriendItem> mFriendItemList;
	private final Fragment mFragment;

	static class ViewHolder {
		public TextView text;
		public LinearLayout imageLayout;
	}

	public FriendsListAdapter(Fragment fragment) {
		mFragment = fragment;
		mFriendItemList = new ArrayList<FriendItem>();
	}
	
	public void addItem(FriendItem friend){
		mFriendItemList.add(friend);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// Check if we can reuse the view
		LayoutInflater inflater = mFragment.getActivity().getLayoutInflater();
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.friend_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.friend_name);
			viewHolder.imageLayout = (LinearLayout) rowView
					.findViewById(R.id.photo_layout);
			rowView.setTag(viewHolder);
		}
		// Populate the view contents
		ViewHolder holder = (ViewHolder) rowView.getTag();
		FriendItem friend = mFriendItemList.get(position);
		holder.text.setText(friend.getName());
		holder.imageLayout.removeAllViews();
		holder.imageLayout.addView(friend.getPictureView(inflater));

		return rowView;
	}

	@Override
	public int getCount() {
		return mFriendItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFriendItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void removeAll() {
		for (FriendItem item : mFriendItemList){
			item.clearCache();
		}
		mFriendItemList.clear();
		notifyDataSetChanged();
	}
}
