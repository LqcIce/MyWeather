package edu.hrbeu.myweather;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.hrbeu.myweather.AsyncImageLoader.ImageCallback;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class SharewearActivity extends Activity {

	PopupWindow popWindow;
	/***
	 * ʹ����������ջ�ȡͼƬ
	 */
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	/***
	 * ʹ������е�ͼƬ
	 */
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	public static final String SAVED_IMAGE_DIR_PATH = "photo_path";
	private Uri photoUri;
	private String picPath;
	private Intent lastIntent;
	private GridView mygridview;

	String PathUrl;
	String[] ImagePathData;
	private Bitmap pic;
	ImageView test;

	// SoftReference�������ã���Ϊ�˸��õ�Ϊ��ϵͳ���ձ���
	private HashMap<String, SoftReference<Drawable>> imageCache;
	Drawable drawable;
	private AsyncImageLoader asyncImageLoader;

	public void AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharewear);

		String url = "http://192.168.100.107:8000/photo/";
		PathUrl = "http://192.168.100.107:8000/static/";
		String uploadFile = Environment.getExternalStorageDirectory()
				+ "/Penguins.jpg";

		test = (ImageView) findViewById(R.id.test);

		getAllImage(url);

		ImageView getCamera = (ImageView) findViewById(R.id.getCamera);
		getCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopuptWindow(v);
			}
		});

		lastIntent = getIntent();

		/*
		 * Async_Upload au= new Async_Upload(SharewearActivity.this,uploadFile);
		 * au.execute();
		 */

	}

	private void PopuptWindow(View v) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		/*
		 * LayoutInflater layoutInflater = (LayoutInflater) (MainActivity.this)
		 * .getSystemService(LAYOUT_INFLATER_SERVICE);
		 */
		// ��ȡ�Զ��岼���ļ�poplayout.xml����ͼ
		View popview = layoutInflater.inflate(R.layout.getcamera_popview, null);
		popWindow = new PopupWindow(popview, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		// �涨������λ��

		int[] location = new int[2];
		v.getLocationOnScreen(location);
		// popWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0]-
		// popWindow.getWidth(),
		// location[1] - popWindow.getHeight());
		popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 110);
		// PopupWindow���
		// TextView i1pop = (TextView) popview.findViewById(R.id.i1pop);
		// i1pop.setText(text);
		TextView getcamera_pop = (TextView) popview
				.findViewById(R.id.getcamera_pop);
		TextView getphotos_pop = (TextView) popview
				.findViewById(R.id.getphotos_pop);
		getcamera_pop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto();
			}
		});
		getphotos_pop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pickPhoto();
			}
		});
	}

	/**
	 * ���ջ�ȡͼƬ
	 */
	private void takePhoto() {
		// ִ������ǰ��Ӧ�����ж�SD���Ƿ����
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
			/***
			 * ��Ҫ˵��һ�£����²���ʹ����������գ����պ��ͼƬ����������е� ����ʹ�õ����ַ�ʽ��һ���ô����ǻ�ȡ��ͼƬ�����պ��ԭͼ
			 * �����ʵ��ContentValues�����Ƭ·���Ļ������պ��ȡ��ͼƬΪ����ͼ������
			 */
			ContentValues values = new ContentValues();
			photoUri = this.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			/** ----------------- */
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		} else {
			Toast.makeText(this, "�ڴ濨������", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * �������ȡͼƬ s
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			doPhoto(requestCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ѡ��ͼƬ�󣬻�ȡͼƬ��·��
	 * 
	 * @param requestCode
	 * @param data
	 */
	private void doPhoto(int requestCode, Intent data) {
		if (requestCode == SELECT_PIC_BY_PICK_PHOTO) // �����ȡͼƬ����Щ�ֻ����쳣�������ע��
		{
			if (data == null) {
				Toast.makeText(this, "ѡ��ͼƬ�ļ�����", Toast.LENGTH_LONG).show();
				return;
			}
			photoUri = data.getData();
			if (photoUri == null) {
				Toast.makeText(this, "ѡ��ͼƬ�ļ�����", Toast.LENGTH_LONG).show();
				return;
			}
		}
		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			cursor.close();
		}
		Log.i("aaa", "imagePath = " + picPath);
		if (picPath != null
				&& (picPath.endsWith(".png") || picPath.endsWith(".PNG")
						|| picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {

			Intent startEx = new Intent(SharewearActivity.this,
					PhotoPreActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(SAVED_IMAGE_DIR_PATH, picPath);
			startEx.putExtras(bundle);
			setResult(Activity.RESULT_FIRST_USER, startEx);
			startActivity(startEx);

			// lastIntent.putExtra(SAVED_IMAGE_DIR_PATH, picPath);
			// setResult(Activity.RESULT_OK, lastIntent);
			// finish();
		} else {
			Toast.makeText(this, "ѡ��ͼƬ�ļ�����ȷ", Toast.LENGTH_LONG).show();
		}
	}

	public void getAllImage(final String url) {
		Thread newThread; // ����һ�����߳�
		newThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// ����д�����߳���Ҫ���Ĺ���

				try {
					// ����һ��Ĭ�ϵ�HttpClient
					HttpClient httpclient = new DefaultHttpClient();
					// ����һ��GET����
					HttpGet request = new HttpGet(url);
					Log.v("response 3text", url);
					// ����GET���󣬲�����Ӧ����ת�����ַ���
					String response = httpclient.execute(request,
							new BasicResponseHandler());
					Log.v("response 3text", response);
					ImagePathData = getImagePath(response);
					Message m = new Message();
					myHandler.sendMessage(m);// ������Ϣ:ϵͳ���Զ�����handleMessage������������Ϣ

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		newThread.start(); // �����߳�
	}

	public String[] getImagePath(String strResult) {

		try {
			// /����

			JSONArray jsonFiles = new JSONArray(strResult);
			String[] ImagePath = new String[jsonFiles.length()];
			for (int i = 0; i < jsonFiles.length(); i++) {
				JSONObject jsonFile = (JSONObject) jsonFiles.get(i);

				JSONObject jsobfields = (JSONObject) jsonFile
						.getJSONObject("fields");

				ImagePath[i] = jsobfields.getString("headImg").substring(7);

			}
			return ImagePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			mygridview = (GridView) findViewById(R.id.mygridview);

			for (int i = 0; i < ImagePathData.length; i++) {
				String ImagePathUrl = PathUrl + ImagePathData[i];
				final int j = i;
				AsyncImageLoader myAsyncImageLoader = new AsyncImageLoader();
				myAsyncImageLoader.loadDrawable(ImagePathUrl,
						new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {
								// ���GridViewʵ��
								if (mygridview != null) {
									getAdapterDrawable(imageDrawable, j);
								}
							}
						});// ���ͼ����Դ��ID

				Log.v("ttt", ImagePathUrl);

			}

			super.handleMessage(msg);

		}
	};

	List<Map<String, Object>> imageArray = new ArrayList<Map<String, Object>>();

	int count=0;
	public void getAdapterDrawable(Drawable imageDrawable, int i) {

		Map<String, Object> imageMap = new HashMap<String, Object>();

		imageMap.put("imageItem", imageDrawable);
		imageMap.put("textItem", "icon" + i);// ��������ItemText
		Log.v("ttt", "1111111111111111111111image"+i);
		imageArray.add(imageMap);
		// ʵ����һ��������
		count++;
		
		if(count==ImagePathData.length)
			complete();
	}

	public void complete() {
		SimpleAdapter adapter = new SimpleAdapter(SharewearActivity.this,
				imageArray, R.layout.sharewear_item, new String[] {
						"imageItem", "textItem" }, new int[] { R.id.image_item,
						R.id.text_item });

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if ((view instanceof ImageView) & (data instanceof Drawable)) {
					ImageView iv = (ImageView) view;
					Drawable drawable = (Drawable) data;
					iv.setBackground(drawable);
					return true;
				}
				return false;

			}

		});

		// ΪGridView����������
		mygridview.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sharewear, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
