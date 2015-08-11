package com.cj.dreams.video.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingleImageCircleUtil extends AsyncTask<String, Void, Bitmap> {

	private ImageView iv;

	public SingleImageCircleUtil(ImageView iv) {
		this.iv = iv;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		try {
			bitmap = SimpleImageLoader.getBitmap(params[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (result != null) {
			iv.setImageBitmap(result);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	public static class SimpleImageLoader {
		public static Bitmap getBitmap(String urlStr) throws IOException {
			Bitmap bitmap;
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5 * 1000);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();


















			return bitmap;
		}
	}

}
