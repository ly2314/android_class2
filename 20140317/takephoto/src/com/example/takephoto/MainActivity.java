package com.example.takephoto;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE_TAKE_PHOTO = 0;
	private ImageView imageView;
	private TextView filePath;
	private TextView urlPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Parse.initialize(this, "TBj7tIT2KdaoddftugUeL9hKgIB925grhDKrWvCM",
				"oYOs2OPOZidrK5RLUwH6XhU6JGvwAK9v5ybMkc4Q");

		setContentView(R.layout.activity_main);

		imageView = (ImageView) findViewById(R.id.imageView1);
		filePath = (TextView) findViewById(R.id.filePath);
		urlPath = (TextView) findViewById(R.id.urlPath);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d("debug", "settings");
			return true;
		case R.id.action_takephoto:
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, getStoreFile());
			startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
			if (resultCode == RESULT_OK) {
				Log.d("debug", "extraFile = " + getStoreFile());
//				Bitmap image = (Bitmap) data.getExtras().get("data");
//				save(image);
//				imageView.setImageBitmap(image);
			}
		}
	}

	private Uri getStoreFile() {
		File imageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (!imageDir.exists()) {
			imageDir.mkdir();
		}

		File imageFile = new File(imageDir, "photo_extra.png");
		return Uri.fromFile(imageFile);
	}

	private void save(Bitmap bitmap) {
		File imageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (!imageDir.exists()) {
			imageDir.mkdir();
		}

		File imageFile = new File(imageDir, "photo.png");

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(imageFile));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);

			baos.flush();
			baos.close();

			bos.flush();
			bos.close();

			ParseObject object = new ParseObject("photo");

			final ParseFile file = new ParseFile("photo.png",
					baos.toByteArray());
			file.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					Log.d("debug", file.getUrl());
					urlPath.setText(file.getUrl());
				}
			});

			object.put("file", file);
			object.saveInBackground();

			Log.d("debug", "filePath=" + imageFile.getAbsolutePath());
			filePath.setText(imageFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
