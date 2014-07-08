package com.balieiro.facebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest.Builder;
import com.facebook.internal.ImageRequest.Callback;
import com.facebook.internal.ImageResponse;

public class FriendItem {
	private String mUid;
	private String mName;
	private String mPicPath;
	private ImageView mPicView;
	private String mPicFileName;
	private LoadPictureTask mPictureLoader;
	
	// Adding a private constructor in here to avoid this class being instantiated
	// in the wrong manner.
	private FriendItem(){};
	
	public static FriendItem getInstance(JSONObject friend) {
		try{
			FriendItem instance = new FriendItem();
			instance.mUid = friend.getString("id");
			instance.mName = friend.getString("name");
			JSONObject picture = friend.getJSONObject("picture");
			JSONObject data = picture.getJSONObject("data");
			String url = data.getString("url");
			instance.mPicPath = url;
			int index = url.lastIndexOf('/');
			instance.mPicFileName = url.substring(index + 1);
			instance.mPicView = null;
            return instance;

        }catch(JSONException e){
            e.printStackTrace();
        }
		return null;
	}

	public String getName() {
		return mName;
	}

	public Drawable getPicture() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserId() {
		return mUid;
	}

	public View getPictureView(LayoutInflater inflater) {
		if (mPicView == null){
			LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.picture_layout, null);
			mPicView = (ImageView) layout.findViewById(R.id.friend_image);
			mPictureLoader = new LoadPictureTask();
			mPictureLoader.execute();
		}
		return mPicView.getRootView();
	}
	private class LoadPictureTask extends AsyncTask<Void, Void, Void> {

		private Bitmap thumbnail;

		protected Void doInBackground(Void... v) {
			try {
				File filePath = MyFacebookApp.getContext().getFileStreamPath(mPicFileName);
				FileInputStream fi = new FileInputStream(filePath);
				// At this point we know the picture is stored locally.
				thumbnail = BitmapFactory.decodeStream(fi);
				fi.close();
			} catch (FileNotFoundException e) {
				// Start an async download request to get the image
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(Void... v) {}

		protected void onPostExecute(Void v) {
			// Start image download if picture is not present in cache
			if (thumbnail == null) downloadImageBitmap();
			else if (mPicView != null) mPicView.setImageBitmap(thumbnail);
		}

	}
	private void downloadImageBitmap(){
		try {
			URI imageURI = new URI(mPicPath);
			Builder builder = new Builder(MyFacebookApp.getContext(), imageURI);
			builder.setCallback(new Callback(){
				@Override
				public void onCompleted(ImageResponse response) {
					saveAndReloadView(response.getBitmap());
				}
			});
			ImageDownloader.downloadAsync(builder.build());
		} catch (Exception e){}

	}
	
	private void saveAndReloadView(Bitmap thumbnail){
		if (thumbnail == null) return;
		// In here we will make this picture to be rounded to look fancy in the list.
		thumbnail = getRoundedBitmap(thumbnail, R.drawable.picture_mask);
		mPicView.setImageBitmap(thumbnail);
		// Use the compress method on the Bitmap object to write image to
		// the OutputStream
		FileOutputStream fos;
		try {
			fos = MyFacebookApp.getContext().openFileOutput(mPicFileName, 
					Context.MODE_PRIVATE);
			// Writing the bitmap to the output stream
			thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Bitmap getRoundedBitmap(Bitmap source, int pictureMask){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		  // Starting with Honeycomb, we can load the bitmap as mutable.
		  options.inMutable = true;
		}
		// We could also use ARGB_4444, but not RGB_565 (we need an alpha layer).
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap;
		if (source.isMutable()) {
		  bitmap = source;
		} else {
		  bitmap = source.copy(Bitmap.Config.ARGB_8888, true);
		  source.recycle();
		}
		// The bitmap is opaque, we need to enable alpha compositing.
		bitmap.setHasAlpha(true);
		
		
		Canvas canvas = new Canvas(bitmap);
		Bitmap mask = BitmapFactory.decodeResource(MyFacebookApp.getContext().
				getResources(), pictureMask);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(mask, 0, 0, paint);
		// We do not need the mask bitmap anymore.
		mask.recycle();
		
		return bitmap;
	}

	public void clearCache() {
		mPictureLoader.cancel(true);
		mPicView = null;
	}
}