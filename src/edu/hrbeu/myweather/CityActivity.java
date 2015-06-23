package edu.hrbeu.myweather;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class CityActivity extends Activity {

	EditText searchcity;
	ListView searchlist;
	/* SharedPreferences sp; */
	SharedPreferences sp2;
	DBUtil myDbHelper;
	WDataCache wDataCache;

	String mycityname;

	ArrayList<String> nowlists;

	TextView myLocation;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	String locaName;

	TextView hotcity11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		// ���������
		getWindow().setSoftInputMode(
		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		/* sp = getSharedPreferences("mycity", MODE_PRIVATE); */
		sp2 = getSharedPreferences("nowcity", MODE_PRIVATE);

		searchcity = (EditText) findViewById(R.id.searchcity);

		searchlist = (ListView) findViewById(R.id.searchlist);

		// DBUtil myDbHelper = new DBUtil(null);
		myDbHelper = new DBUtil(this);
		wDataCache = new WDataCache(this);
		wDataCache.open();

		try {
			myDbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			myDbHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		/*
		 * searchbutton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub // ��ѯ��������������string��û���򷵻�null
		 * 
		 * mycityname = searchcity.getText().toString(); addCity(mycityname); }
		 * });
		 */

		searchcity.addTextChangedListener((new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String searchcityV = searchcity.getText().toString();
				if (searchcityV != null && !"".equals(searchcityV.trim())) {
					// Log.i(TAG , " searchcityV:"+searchcityV);
					nowlists = myDbHelper.selectCity(searchcityV);// ���ݹؼ��ֲ�ѯ
					ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
							CityActivity.this, R.layout.list_style, nowlists);
					searchlist.setAdapter(arrayAdapter);
					searchlist
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									String cityname = nowlists.get(position);
									addCity(cityname);
								}
							});
				} else {
					searchlist.setAdapter(null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		}));

		/*
		 * // ע��㲥 IntentFilter filter = new IntentFilter();
		 * filter.addAction(Common.LOCATION_ACTION); this.registerReceiver(new
		 * LocationBroadcastReceiver(), filter);
		 * 
		 * // �������� Intent intent = new Intent(); intent.setClass(this,
		 * LocationSvc.class); startService(intent);
		 */
		myLocation = (TextView) findViewById(R.id.mylocation);
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		myLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InitLocation();
				mLocationClient.start();

			}
		});

		hotcity11 = (TextView) findViewById(R.id.hotcity11);
		hotcity11.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String hotcityname11 = hotcity11.getText().toString();
				addCity(hotcityname11);
			}
		});

		/*
		 * // �ȴ���ʾ dialog = new ProgressDialog(this);
		 * dialog.setMessage("���ڶ�λ..."); dialog.setCancelable(true);
		 * dialog.show();
		 */

	}

	public void addCity(String mycityname) {
		String citycode = myDbHelper.queryOneData(mycityname);
		if (citycode != null) {
			/*
			 * Editor ed = sp.edit();
			 * ed.putString(searchcity.getText().toString(), citycode); //
			 * ed.putString("searchcity", searchcity.getText() // .toString());
			 * ed.commit();
			 */

			Editor ed2 = sp2.edit();
			ed2.putString("citycode", citycode);
			ed2.putString("searchcity", mycityname);
			ed2.commit();

			Cursor cCursor = wDataCache.getmyWeatherDB(citycode);
			if (cCursor == null || cCursor.getCount() <= 0) {
				wDataCache.insertmyWeatherDB(mycityname, citycode, null, null);
			} else {
				Toast.makeText(CityActivity.this,
						"���Ѿ����" + mycityname + "�ˣ���ȥ������", Toast.LENGTH_SHORT)
						.show();
			}

			Intent intent = new Intent(CityActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			Toast.makeText(CityActivity.this, "û���ҵ���Ӧ����", Toast.LENGTH_SHORT)
					.show();
			searchcity.setText("");
		}
	}

	/*
	 * private class LocationBroadcastReceiver extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { if
	 * (!intent.getAction().equals(Common.LOCATION_ACTION)) return; String
	 * locationInfo = intent.getStringExtra(Common.LOCATION);
	 * location.setText(locationInfo); dialog.dismiss();
	 * CityActivity.this.unregisterReceiver(this);// ����Ҫʱע�� } }
	 */

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				locaName = location.getAddrStr();
				myLocation.setText("��λ�����" + locaName);

				sb.append("\nprovince");
				// ʡ
				sb.append(location.getProvince());
				// ��
				sb.append("\ncity");
				sb.append(location.getCity());
				// �غ���
				sb.append("\ndistrict");
				sb.append(location.getDistrict());
				String locaCityName = location.getCity();
				locaCityName = locaCityName.substring(0, locaCityName.length() - 1);
				String locaDistrictName = location.getDistrict();
				if(locaDistrictName.length() >= 3){
				locaDistrictName = locaDistrictName.substring(0, locaDistrictName.length() - 1);
				}

				String citycode = myDbHelper.queryOneData(locaDistrictName);
				if (citycode != null) {
					addCity(locaDistrictName);
				} else {
					addCity(locaCityName);
				}

			}

			// logMsg(sb.toString());
			Log.i("CCCC", sb.toString());
			mLocationClient.stop();
			
		}
	}

	/**
	 * ��ʾ�����ַ���
	 * 
	 * @param str
	 */
	// public void logMsg(String str) {
	// try {
	// if (mLocationResult != null)
	// mLocationResult.setText(str);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// ���ö�λģʽ
		option.setCoorType(tempcoor);// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		// int span=1000;
		int span = 5000;
		// try {
		// span = Integer.valueOf(frequence.getText().toString());
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		option.setScanSpan(span);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		// option.setIsNeedAddress(checkGeoLocation.isChecked());
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
}
