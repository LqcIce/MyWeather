package edu.hrbeu.myweather;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.hrbeu.myweather.SlideMenu;
import edu.hrbeu.myweather.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnGestureListener,
		OnTouchListener, OnClickListener {

	// ����һ��GestureDetector(����ʶ����)���������
	private GestureDetector myGestureDetector;
	/* private PageControlView pageControl; */
	private SlideMenu slideMenu;

	EncodeUtil jd;
	Weather myWeather = new Weather();
	Index myIndex = new Index();
	TextView date;
	TextView viewday;
	TextView temperature;
	TextView windD;
	TextView windP;
	ImageView phenomena;

	TextView sundown;
	TextView sunrise;

	String url_f;
	String url_i;

	Button citybutton;

	int page = 0;// onfling ���ڼ�¼����ҳ��������ȡ��ѭ������

	// ����һ��ViewFlipper���������
	ViewFlipper myViewFlipper;

	private TextView weather_condition;
	String[] WeatherCondition = { "��", "����", " ��", " ����", " ������", " ��������б���",
			" ���ѩ", " С��", " ����", " ����", " ����", " ����", " �ش���", " ��ѩ", " Сѩ",
			" ��ѩ", " ��ѩ", " ��ѩ", " ��", " ����", " ɳ����", " С������", " �е�����",
			" �󵽱���", " ���굽����", " ���굽�ش���", " С����ѩ", " �е���ѩ", " �󵽱�ѩ", " ����",
			" ��ɳ", " ǿɳ����", " ��", " ��" };
	String[] windDirect = { "�޳�������", "������", "����", "���Ϸ�", "�Ϸ�", "���Ϸ�", "����",
			"������", "����", "��ת��" };
	String[] windPower = { "΢��", "3-4��", "4-5��", "5-6��", "6-7��", "7-8��",
			"8-9��", "9-10��", "10-11��", "11-12��" };

	int[] DWeatherArray = { R.drawable.d00, R.drawable.d01, R.drawable.d02,
			R.drawable.d03, R.drawable.d04, R.drawable.d05, R.drawable.d06,
			R.drawable.d07, R.drawable.d08, R.drawable.d09, R.drawable.d10,
			R.drawable.d11, R.drawable.d12, R.drawable.d13, R.drawable.d14,
			R.drawable.d15, R.drawable.d16, R.drawable.d17, R.drawable.d18,
			R.drawable.d19, R.drawable.d20, R.drawable.d21, R.drawable.d22,
			R.drawable.d23, R.drawable.d24, R.drawable.d25, R.drawable.d26,
			R.drawable.d27, R.drawable.d28, R.drawable.d29, R.drawable.d30,
			R.drawable.d31, R.drawable.d53 };

	int[] NWeatherArray = { R.drawable.n00, R.drawable.n01, R.drawable.n02,
			R.drawable.n03, R.drawable.n04, R.drawable.n05, R.drawable.n06,
			R.drawable.n07, R.drawable.n08, R.drawable.n09, R.drawable.n10,
			R.drawable.n11, R.drawable.n12, R.drawable.n13, R.drawable.n14,
			R.drawable.n15, R.drawable.n16, R.drawable.n17, R.drawable.n18,
			R.drawable.n19, R.drawable.n20, R.drawable.n21, R.drawable.n22,
			R.drawable.n23, R.drawable.n24, R.drawable.n25, R.drawable.n26,
			R.drawable.n27, R.drawable.n28, R.drawable.n29, R.drawable.n30,
			R.drawable.n31, R.drawable.n53 };

	private View view_today;

	private View view_tomorrow;

	private View view_afterday;
	private ListView cityLV;
	private SharedPreferences sp2;
	private ArrayList<String> cityList;
	private ArrayList<String> codeList;
	private TextView citytitle;
	private TextView daytitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		WDataCache WDataCache = new WDataCache(MainActivity.this);
		WDataCache.open();
		
		WDataCache.insertmyWeatherDB("��̨","123", "456");
		
		// ////////////////////////////////////////////////////////////////
		// ��ȡURL
		SharedPreferences sp = getSharedPreferences("mycity", MODE_PRIVATE);
		sp2 = getSharedPreferences("nowcity", MODE_PRIVATE);//��ǰ��ʾ�ĳ��д����������
		
		// String areaid = sp.getString("citycode", "101010100");

		Map<String, ?> cityMap = sp.getAll();// ��ȡsp�����м�ֵ��
		cityList = new ArrayList<String>();
		codeList = new ArrayList<String>();

		for (Map.Entry<String, ?> entry : cityMap.entrySet()) {
			System.out.println("key= " + entry.getKey() + " and value= "
					+ entry.getValue());
			cityList.add(entry.getKey());
			codeList.add(entry.getValue().toString());
		}

		String areaid = sp2.getString("citycode", "101010100");

		url_f = EncodeUtil.getUrl(areaid, "forecast_v");// ����
		url_i = EncodeUtil.getUrl(areaid, "index_v");// ָ��
		// //////////////////////////////////////////////////////////////
		// �ص㣬��Context������LayoutInflater.from()��õ�LayoutInflater����
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);

		// ��inflate(��Ⱦ)�����������ļ���ΪView����
		view_today = factory.inflate(R.layout.view_today, null);
		view_tomorrow = factory.inflate(R.layout.view_tomorrow, null); 
		view_afterday = factory.inflate(R.layout.view_afterday, null);

		// ��inflate�ؼ��������޷�ʹ����
		myViewFlipper = (ViewFlipper) findViewById(R.id.myViewFlipper);

		/* pageControl = (PageControlView) findViewById(R.id.); */
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		ImageView menuImage = (ImageView) findViewById(R.id.title_bar_menu_btn);
		menuImage.setOnClickListener(this);

		
		citytitle=(TextView) findViewById(R.id.citytitle);
		daytitle=(TextView) findViewById(R.id.daytitle);
		viewday = (TextView) findViewById(R.id.viewday);
		
		// //////////////////////////////////////////////////////////////
		// �����б�
		cityLV = (ListView) findViewById(R.id.citylist);

		ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
				R.layout.list_style, cityList);

		cityLV.setAdapter(cityAdapter);
		cityLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				Editor ed2 = sp2.edit();
				ed2.putString("citycode", codeList.get(position));
				ed2.putString("searchcity", cityList.get(position));
				ed2.commit();
				
				//�����µĻ�ȡ����������ַ
				url_f = EncodeUtil.getUrl(codeList.get(position), "forecast_v");// ����
				url_i = EncodeUtil.getUrl(codeList.get(position), "index_v");// ָ��
				//��ȡ�µĳ�����������
				getWeatherDate(url_f, 1);
				getWeatherDate(url_i, 2);
				
				if (slideMenu.isMenuShow()) 
					slideMenu.hideMenu();
				
			}
		});

		// ��addView���������ɵ�View������뵽ViewFlipper������
		myViewFlipper.addView(view_today);
		myViewFlipper.addView(view_tomorrow);
		myViewFlipper.addView(view_afterday);

		// MainActivity�̳���OnGestureListener�ӿ�
		myGestureDetector = new GestureDetector(this);
		// ����ʶ�𳤰����ƣ���������ʵ���϶�
		myViewFlipper.setLongClickable(true);
		// MainActivity�̳���OnTouchListener�ӿ� ��myViewFlipper���ô����¼�������
		myViewFlipper.setOnTouchListener(this);
		myViewFlipper.setDisplayedChild(0);
		// ///////////////////////////////////////////////////////////////////
		citybutton = (Button) findViewById(R.id.citybutton);
		citybutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						CityActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getWeatherDate(url_f, 1);
		getWeatherDate(url_i, 2);
		System.out.println("onResume");
	}

	public void getWeatherDate(final String url, final int num) {
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
					Log.v("response text", url);
					// ����GET���󣬲�����Ӧ����ת�����ַ���
					String response = httpclient.execute(request,
							new BasicResponseHandler());
					Log.v("response text", response);

					if (num == 1)
						myWeather = getWeather(response);
					else
						myIndex = getIndex(response);

					Message m = new Message();
					m.what = num;
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

	public void changeview(View view) {
		sunrise = (TextView) view.findViewById(R.id.sunrise);
		sundown = (TextView) view.findViewById(R.id.sundown);
		/*date = (TextView) view.findViewById(R.id.date);*/
		
		temperature = (TextView) view.findViewById(R.id.temperature);
		windD = (TextView) view.findViewById(R.id.windD);
		windP = (TextView) view.findViewById(R.id.windP);
		phenomena = (ImageView) view.findViewById(R.id.phenomena);
		// TextView city = (TextView)findViewById(R.id.city);
		weather_condition = (TextView) view
				.findViewById(R.id.weather_condition);
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
				citytitle.setText(myWeather.city);
				changeview(view_today);
				RefreshWeather(0);
				changeview(view_tomorrow);
				RefreshWeather(1);
				changeview(view_afterday);
				RefreshWeather(2);// ���½�����ʾ
			}
			if (msg.what == 2) {
				RefreshIndex();
			}

			super.handleMessage(msg);

		}
	};

	public void RefreshWeather(int i) {
		// TODO Auto-generated method stub
		/*date.setText(myWeather.date);*/
        
		/*String viewdays = getDateStr(i);
		viewday.setText(viewdays);

		String[] daytitles = {"����","����","����"};
		daytitle.setText(daytitles[i]);*/
		// �ָ����ճ�����ʱ��sunrises��sundowns(�ַ�����ʽ)
		days(0);
		String[] suntimes = myWeather.suntime[0].split("\\|", 2);
		String sunrises, sundowns;
		sunrises = suntimes[0];
		sundowns = suntimes[1];

		sunrise.setText("�ճ�ʱ��:" + sunrises);
		sundown.setText("����ʱ��:" + sundowns);

		// ////////////////////////////////////////////
		Date dt = new Date();// �������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
								// HH:mm:ss��ʽ��ʾ201506051830
		int hh = dt.getHours();
		Boolean dayflag = true;

		// �������ϵĲ�����ͬ��ͨ���ճ�����ʱ������ж����
		if (hh >= 18 || hh < 8)
			dayflag = false;

		if (dayflag) {
			windD.setText(windDirect[Integer.parseInt(myWeather.windDD[i])]);
			windP.setText(windPower[Integer.parseInt(myWeather.windPD[i])]);

			phenomena.setBackgroundDrawable(getResources().getDrawable(
					DWeatherArray[Integer.parseInt(myWeather.weatherD[i])]));

			weather_condition.setText(WeatherCondition[Integer
					.parseInt(myWeather.weatherD[i])]);
			temperature.setText(myWeather.temperatureD[i]+"��");
		} else {
			Log.v("TP", "TP4");
			windD.setText(windDirect[Integer.parseInt(myWeather.windDN[i])]);
			windP.setText(windPower[Integer.parseInt(myWeather.windPN[i])]);

			phenomena.setBackgroundDrawable(getResources().getDrawable(
					DWeatherArray[Integer.parseInt(myWeather.weatherN[i])]));

			weather_condition.setText(WeatherCondition[Integer
					.parseInt(myWeather.weatherN[i])]);
			temperature.setText(myWeather.temperatureN[i]+"��");
		}
	}

	/**
	 * ��ȡ����Ԥ��
	 * 
	 * @param strResult
	 * @return
	 */
	public Weather getWeather(String strResult) {

		try {
			String suntime;
			// /����
			JSONObject jsonObject;
			// String a = new String(strResult, "UTF-8");
			jsonObject = new JSONObject(strResult);

			JSONObject c = jsonObject.getJSONObject("c");
			JSONObject f = jsonObject.getJSONObject("f");

			Weather weather = new Weather();
			weather.city = convertWord(c.getString("c3"));
			weather.province = c.getString("c7");
			weather.date = f.getString("f0");

			JSONArray f1 = f.getJSONArray("f1");

			for (int i = 0; i < f1.length(); i++) {

				JSONObject jsob = (JSONObject) f1.get(i);

				weather.weatherD[i] = jsob.getString("fa");
				weather.weatherN[i] = jsob.getString("fb");
				weather.temperatureD[i] = jsob.getString("fc");
				weather.temperatureN[i] = jsob.getString("fd");
				weather.windDD[i] = jsob.getString("fe");
				weather.windDN[i] = jsob.getString("ff");
				weather.windPD[i] = jsob.getString("fg");
				weather.windPN[i] = jsob.getString("fh");
				weather.suntime[i] = jsob.getString("fi");// �ճ�����ʱ�䣬һ�����ַ����������ָ�

			}
			return weather;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ȡ����ָ����Ϣ
	 * 
	 * @param strResult
	 * @return
	 */
	public Index getIndex(String strResult) {

		try {
			// /����
			JSONObject jsonObject;
			// String a = new String(strResult, "UTF-8");
			jsonObject = new JSONObject(strResult);
			Index index = new Index();
			JSONArray i = jsonObject.getJSONArray("i");
			for (int j = 0; j < i.length(); j++) {

				JSONObject jsob = (JSONObject) i.get(j);

				index.i_s[j] = convertWord(jsob.getString("i1"));
				index.i_c[j] = convertWord(jsob.getString("i2"));
				index.i_c2[j] = convertWord(jsob.getString("i3"));
				index.i_l[j] = convertWord(jsob.getString("i4"));
				index.i_5[j] = convertWord(jsob.getString("i5"));
			}
			return index;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ˢ������ָ��
	 */
	public void RefreshIndex() {

		TextView i1 = (TextView) view_today.findViewById(R.id.i1);
		TextView i2 = (TextView) view_today.findViewById(R.id.i2);
		TextView i3 = (TextView) view_today.findViewById(R.id.i3);

		// i1.setText(myIndex.i_s[0] + "," + myIndex.i_c[0] + ","
		// + myIndex.i_c2[0] + "," + myIndex.i_l[0] + "," + myIndex.i_5[0]);
		// i2.setText(myIndex.i_s[1] + "," + myIndex.i_c[1] + ","
		// + myIndex.i_c2[1] + "," + myIndex.i_l[1] + "," + myIndex.i_5[1]);
		// i3.setText(myIndex.i_s[2] + "," + myIndex.i_c[2] + ","
		// + myIndex.i_c2[2] + "," + myIndex.i_l[2] + "," + myIndex.i_5[2]);
		i1.setText(myIndex.i_c[0] + ":" + myIndex.i_l[0] + "\n"
				+ myIndex.i_5[0]);
		i2.setText(myIndex.i_c[1] + ":" + myIndex.i_l[1] + "\n"
				+ myIndex.i_5[1]);
		i3.setText(myIndex.i_c[2] + ":" + myIndex.i_l[2] + "\n"
				+ myIndex.i_5[2]);
	}

	/**
	 * ����������ת��Ϊ��������
	 * 
	 * @param before
	 * @return
	 */
	public String convertWord(String before) {
		byte[] converttoBytes = null;
		try {
			converttoBytes = before.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String after = new String(converttoBytes);
		System.out.println(after);

		return after;
	}

	/*
	 * * ��ȡָ���պ� �� dayAddNum ��� ����
	 * 
	 * @param day ���ڣ���ʽΪString��"2013-9-3";
	 * 
	 * @param dayAddNum �������� ��ʽΪint;
	 * 
	 * @return
	 */
	public String getDateStr(int dayAddNum) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd");
		Date nowDate = new Date();
		Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60
				* 1000);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
		String dateOk = simpleDateFormat.format(newDate2);
		return dateOk;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	// ʵ��OnFling�������Ϳ������û�������ʼ����ʶ������һ��������ƣ�������
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		// ����e1�ǰ����¼���e2�Ƿſ��¼���ʣ�������ǻ������ٶȷ����������ò���
		// ����ʱ�ĺ�������ڷſ�ʱ�ĺ����꣬�������󻬶�
		if (slideMenu.isMenuShow()) {
			slideMenu.hideMenu();
		} else {
			if ((e1.getX() > e2.getX()) && (page < 2)) {
				myViewFlipper.showNext();
				page += 1;
				days(page);
			}
			// ����ʱ�ĺ�����С�ڷſ�ʱ�ĺ����꣬�������һ���
			else if ((e1.getX() < e2.getX()) && (page > 0)) {
				myViewFlipper.showPrevious();
				page -= 1;
				days(page);
			} else if ((e1.getX() < e2.getX()) && (page == 0)) {
				slideMenu.showMenu();
			}

		}

		return false;
	}
	
	public void days(int day){
		String viewdays = getDateStr(day);
		viewday.setText("��"+viewdays);

		String[] daytitles = {"����","����","����"};
		daytitle.setText(daytitles[day]);
	}

	/*
	 * ʵ��OnTouchListener�ӿ��е�onTouch()��������View�Ϸ�������ʱ��ʱ���ã�����һ��View��һ���˶��¼�event�����ǽ�
	 * ���event����OnGestureListener�ӿڵ�onTouchEvent()���������������ǵ�OnFling()���ܹ�����
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return myGestureDetector.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_bar_menu_btn:
			if (slideMenu.isMenuShow()) {
				slideMenu.hideMenu();
			} else {
				slideMenu.showMenu();
			}
			break;
		}

	}
}