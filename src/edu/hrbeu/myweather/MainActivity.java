package edu.hrbeu.myweather;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnGestureListener,
		OnTouchListener, OnClickListener {

	// 定义一个GestureDetector(手势识别类)对象的引用
	private GestureDetector myGestureDetector;
	/* private PageControlView pageControl; */
	private SlideMenu slideMenu;

	EncodeUtil jd;
	Weather myWeather = new Weather();
	Index myIndex = new Index();
	TextView date;
	TextView viewday;
	TextView nowtemp;
	TextView tempH;
	TextView tempL;
	TextView windD;
	TextView windP;
	ImageView phenomena;

	TextView sundown;
	TextView sunrise;

	String areaid;
	String nowCityN;

	String url_f;
	String url_i;

	Button citybutton;

	String weatherResponse;
	String indexResponse;
	WDataCache wDataCache;

	String tempByBD;

	Button buttonshare;
	String shareContent;

	PopupWindow popWindow;

	int page = 0;// onfling 用于记录滑动页数，用于取消循环滑动

	// 定义一个ViewFlipper对象的引用
	ViewFlipper myViewFlipper;

	private TextView weather_condition;
	String[] WeatherCondition = { "晴", "多云", " 阴", " 阵雨", " 雷阵雨", " 雷阵雨伴有冰雹",
			" 雨夹雪", " 小雨", " 中雨", " 大雨", " 暴雨", " 大暴雨", " 特大暴雨", " 阵雪", " 小雪",
			" 中雪", " 大雪", " 暴雪", " 雾", " 冻雨", " 沙尘暴", " 小到中雨", " 中到大雨",
			" 大到暴雨", " 暴雨到大暴雨", " 大暴雨到特大暴雨", " 小到中雪", " 中到大雪", " 大到暴雪", " 浮尘",
			" 扬沙", " 强沙尘暴", " 霾" };
	String[] windDirect = { "无持续风向", "东北风", "东风", "东南风", "南风", "西南风", "西风",
			"西北风", "北风", "旋转风" };
	String[] windPower = { "微风", "3-4级", "4-5级", "5-6级", "6-7级", "7-8级",
			"8-9级", "9-10级", "10-11级", "11-12级" };

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
	private ArrayList<String> cityArray;
	private ArrayList<String> codeList;
	private TextView citytitle;
	private TextView daytitle;
	private ArrayAdapter<String> cityAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		wDataCache = new WDataCache(MainActivity.this);
		wDataCache.open();

		// ////////////////////////////////////////////////////////////////s
		// 获取URL
		/* SharedPreferences sp = getSharedPreferences("mycity", MODE_PRIVATE); */
		sp2 = getSharedPreferences("nowcity", MODE_PRIVATE);// 当前显示的城市代码放在这了

		// String areaid = sp.getString("citycode", "101010100");

		// for (Map.Entry<String, ?> entry : cityMap.entrySet()) {
		// System.out.println("key= " + entry.getKey() + " and value= "
		// + entry.getValue());
		// cityArray.add(entry.getKey());
		// codeList.add(entry.getValue().toString());
		// }

		areaid = sp2.getString("citycode", null);
		nowCityN = sp2.getString("searchcity", null);

		if (areaid == null) {
			Intent intent = new Intent(MainActivity.this, CityActivity.class);
			startActivity(intent);

			Toast.makeText(MainActivity.this, "请添加一个城市", Toast.LENGTH_SHORT)
					.show();
		}
		url_f = EncodeUtil.getUrl(areaid, "forecast_v");// 天气
		url_i = EncodeUtil.getUrl(areaid, "index_v");// 指数
		// //////////////////////////////////////////////////////////////
		// 重点，将Context对象传入LayoutInflater.from()里，得到LayoutInflater对象
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);

		// 用inflate(渲染)方法将布局文件变为View对象
		view_today = factory.inflate(R.layout.view_today, null);
		view_tomorrow = factory.inflate(R.layout.view_tomorrow, null);
		view_afterday = factory.inflate(R.layout.view_afterday, null);

		// 绑定inflate控件，否则无法使用它
		myViewFlipper = (ViewFlipper) findViewById(R.id.myViewFlipper);

		// nowtemp = (TextView) view_today.findViewById(R.id.nowtemp);

		/* pageControl = (PageControlView) findViewById(R.id.); */
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		ImageView menuImage = (ImageView) findViewById(R.id.title_bar_menu_btn);
		menuImage.setOnClickListener(this);

		citytitle = (TextView) findViewById(R.id.citytitle);
		daytitle = (TextView) findViewById(R.id.daytitle);
		viewday = (TextView) findViewById(R.id.viewday);

		// //////////////////////////////////////////////////////////////
		// 城市列表
		cityLV = (ListView) findViewById(R.id.citylist);

		cityArray = new ArrayList<String>();
		codeList = new ArrayList<String>();

		Cursor cCursor = wDataCache.getAllmyWeatherDB();
		while (cCursor.moveToNext()) {
			cityArray.add(cCursor.getString(cCursor.getColumnIndex("city")));
			codeList.add(cCursor.getString(cCursor.getColumnIndex("citycode")));
		}

		startManagingCursor(cCursor);
		// 创建数组型适配器
		cityAdapter = new ArrayAdapter<String>(this, R.layout.list_style,
				cityArray);
		// ListAdapter cityAdapter = new SimpleCursorAdapter(this,
		// android.R.layout.simple_expandable_list_item_1,
		// cCursor,
		// new String[]{myDbHelper.MyCity},
		// new int[]{R.id.citylist});
		// 显示到列表
		cityLV.setAdapter(cityAdapter);
		cityLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// view.setBackgroundDrawable(getResources().getDrawable(R.drawable.d02));

				Editor ed2 = sp2.edit();
				ed2.putString("citycode", codeList.get(position));
				ed2.putString("searchcity", cityArray.get(position));
				ed2.commit();

				// 生成新的获取城市天气地址
				url_f = EncodeUtil.getUrl(codeList.get(position), "forecast_v");// 天气
				url_i = EncodeUtil.getUrl(codeList.get(position), "index_v");// 指数
				// 获取新的城市天气数据
				getWeatherDate(url_f, 1, cityArray.get(position),
						codeList.get(position));
				getWeatherDate(url_i, 2, cityArray.get(position),
						codeList.get(position));

				if (slideMenu.isMenuShow())
					slideMenu.hideMenu();

				noNetView(codeList.get(position));

			}
		});
		cityLV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				wDataCache.deletemyWeatherDB(codeList.get(position));
				String str = cityArray.get(position);
				// 删掉长按的item
				cityArray.remove(position);
				codeList.remove(position);

				if (cityArray.isEmpty()) {

					Editor ed2 = sp2.edit();
					ed2.clear();
					ed2.commit();

					Intent intent = new Intent(MainActivity.this,
							CityActivity.class);
					startActivity(intent);

					Toast.makeText(MainActivity.this, "请添加一个城市",
							Toast.LENGTH_SHORT).show();
				} else {
					nowCityN = cityArray.get(0);
					areaid = codeList.get(0);

					// 生成新的获取城市天气地址
					url_f = EncodeUtil.getUrl(areaid, "forecast_v");// 天气
					getWeatherDate(url_f, 1, nowCityN, areaid);
					getWeatherDate(url_i, 2, nowCityN, areaid);

					citytitle.setText(nowCityN);

					Editor ed2 = sp2.edit();
					ed2.putString("citycode", areaid);
					ed2.putString("searchcity", nowCityN);
					ed2.commit();
					// nowtemp.setText(tempByBD + "°");

					// 动态更新listview
					cityAdapter.notifyDataSetChanged();

				}

				return false;
			}
		});

		// 用addView方法将生成的View对象加入到ViewFlipper对象中
		myViewFlipper.addView(view_today);
		myViewFlipper.addView(view_tomorrow);
		myViewFlipper.addView(view_afterday);

		// MainActivity继承了OnGestureListener接口
		myGestureDetector = new GestureDetector(this);
		// 设置识别长按手势，这样才能实现拖动
		myViewFlipper.setLongClickable(true);
		// MainActivity继承了OnTouchListener接口 对myViewFlipper设置触屏事件监听器
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
		// ///////////////////////////////////////////////////////////////////
		// 设置分享功能
		buttonshare = (Button) findViewById(R.id.buttonshare);

		// //////////////////////
		// 刷新第一天
		days(0);
	}

	private void startManagingCursor(String cCursor) {
		// TODO Auto-generated method stub

	}

	/* 检测网络连接状态 */
	public boolean CheckNetWork() {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			result = true;/* The net was connected */
		} else {
			result = false;/* The net was bad! */

		}
		return result;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		getWeatherDate(url_f, 1, nowCityN, areaid);
		getWeatherDate(url_i, 2, nowCityN, areaid);

		noNetView(areaid);

		Log.i("cccc", "onResume");
	}

	public void noNetView(String citycode) {
		boolean result = CheckNetWork();
		if (!result) {
			Cursor wCursor = wDataCache.getmyWeatherDB(citycode);
			String weatherColumn = wCursor.getString(wCursor
					.getColumnIndex("weather"));
			String indexColumn = wCursor.getString(wCursor
					.getColumnIndex("windex"));
			myWeather = getWeather(weatherColumn);
			myIndex = getIndex(indexColumn);

			// nowtemp.setText(tempByBD + "°");
			citytitle.setText(myWeather.city);

			changeview(view_today);
			RefreshWeather(0);
			changeview(view_tomorrow);
			RefreshWeather(1);
			changeview(view_afterday);
			RefreshWeather(2);// 更新界面显示
			RefreshIndex();
		}
	}

	public void getWeatherDate(final String url, final int num,
			final String city, final String citycode) {
		Thread newThread; // 声明一个子线程
		Log.i("BBBB", "citycode:" + citycode);
		newThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// 这里写入子线程需要做的工作

				Log.i("CCCC", "citycode:" + citycode);

				String jsonResultByBD = getWeatherByBD(citycode);
				tempByBD = getTempWeatherByBD(jsonResultByBD);

				try {
					// 创建一个默认的HttpClient
					HttpClient httpclient = new DefaultHttpClient();
					// 创建一个GET请求
					HttpGet request = new HttpGet(url);
					Log.v("response text", url);
					// 发送GET请求，并将响应内容转换成字符串
					String response = httpclient.execute(request,
							new BasicResponseHandler());
					Log.v("response text", response);

					if (num == 1) {
						myWeather = getWeather(response);
					} else {
						myIndex = getIndex(response);
					}

					Cursor wCursor = wDataCache.getmyWeatherDB(citycode);
					if (wCursor == null || wCursor.getCount() <= 0) {
						wDataCache.insertmyWeatherDB(city, citycode,
								weatherResponse, indexResponse);
					} else {
						wDataCache.updatemyWeatherDB(city, citycode,
								weatherResponse, indexResponse);
					}

					Message m = new Message();
					m.what = num;
					myHandler.sendMessage(m);// 发送消息:系统会自动调用handleMessage方法来处理消息

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		newThread.start(); // 启动线程
	}

	public void changeview(View view) {
		sunrise = (TextView) view.findViewById(R.id.sunrise);
		sundown = (TextView) view.findViewById(R.id.sundown);
		/* date = (TextView) view.findViewById(R.id.date); */

		tempH = (TextView) view.findViewById(R.id.tempH);
		tempL = (TextView) view.findViewById(R.id.tempL);
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
				// nowtemp.setText(tempByBD + "°");

				changeview(view_today);
				RefreshWeather(0);
				changeview(view_tomorrow);
				RefreshWeather(1);
				changeview(view_afterday);
				RefreshWeather(2);// 更新界面显示
			}
			if (msg.what == 2) {
				RefreshIndex();
			}

			super.handleMessage(msg);

		}
	};

	public void RefreshWeather(int i) {
		// TODO Auto-generated method stub
		/* date.setText(myWeather.date); */

		/*
		 * String viewdays = getDateStr(i); viewday.setText(viewdays);
		 * 
		 * String[] daytitles = {"今天","明天","后天"};
		 * daytitle.setText(daytitles[i]);
		 */
		// 分隔出日出日落时间sunrises，sundowns(字符串格式)

		String[] suntimes = myWeather.suntime[0].split("\\|", 2);
		String sunrises, sundowns;
		sunrises = suntimes[0];
		sundowns = suntimes[1];

		sunrise.setText("日出时间:" + sunrises);
		sundown.setText("日落时间:" + sundowns);

		// ////////////////////////////////////////////
		Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
								// HH:mm:ss格式显示201506051830
		int hh = dt.getHours();
		Boolean dayflag = true;

		// 白天晚上的参数不同，通过日出日落时间进行判读输出
		if (hh >= 18 || hh < 8)
			dayflag = false;
		SharedPreferences sp;
		sp = getSharedPreferences("temperatureD", MODE_PRIVATE);
		if (dayflag) {
			windD.setText(windDirect[Integer.parseInt(myWeather.windDD[i])]);
			windP.setText(windPower[Integer.parseInt(myWeather.windPD[i])]);

			phenomena.setBackgroundDrawable(getResources().getDrawable(
					DWeatherArray[Integer.parseInt(myWeather.weatherD[i])]));
			weather_condition.setText(WeatherCondition[Integer
					.parseInt(myWeather.weatherD[i])]);
			tempH.setText(myWeather.temperatureD[i] + "°");
			tempL.setText(myWeather.temperatureN[i] + "°");
			if (myWeather.temperatureD[0] != null) {
				Editor ed = sp.edit();
				ed.putString("temperatureD", myWeather.temperatureD[0]);
				ed.commit();
			}
			shareContent = myWeather.city + "今天的温度为"
					+ myWeather.temperatureD[0] + "°" + "到"
					+ myWeather.temperatureN[0] + "°" + ","
					+ WeatherCondition[Integer.parseInt(myWeather.weatherD[0])]
					+ "\n" + windDirect[Integer.parseInt(myWeather.windDD[0])]
					+ " " + windPower[Integer.parseInt(myWeather.windPD[0])]
					+ "\n" + myIndex.i_5[2];

			buttonshare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					shareWeather(shareContent);
				}
			});

		} else {
			Log.v("TP", "TP4");
			windD.setText(windDirect[Integer.parseInt(myWeather.windDN[i])]);
			windP.setText(windPower[Integer.parseInt(myWeather.windPN[i])]);
			phenomena.setBackgroundDrawable(getResources().getDrawable(
					NWeatherArray[Integer.parseInt(myWeather.weatherN[i])]));

			weather_condition.setText(WeatherCondition[Integer
					.parseInt(myWeather.weatherN[i])]);

			if (i == 0) {
				String temperatureD = sp.getString("temperatureD",
						myWeather.temperatureD[0]);
				tempH.setText(temperatureD + "°");
				tempL.setText(myWeather.temperatureN[i] + "°");
				shareContent = myWeather.city+"今天的温度为"
						+ temperatureD
						+ "°"
						+ "到"
						+ myWeather.temperatureN[0]
						+ "°"
						+ ","
						+ WeatherCondition[Integer
								.parseInt(myWeather.weatherN[0])] + "\n"
						+ windDirect[Integer.parseInt(myWeather.windDN[0])]
						+ " "
						+ windPower[Integer.parseInt(myWeather.windPN[0])]
						+ "\n" + myIndex.i_5[2];
			} else {
				tempH.setText(myWeather.temperatureD[i] + "°");
				tempL.setText(myWeather.temperatureN[i] + "°");
			}

			buttonshare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					shareWeather(shareContent);

				}
			});
		}
	}

	/**
	 * 获取天气预报
	 * 
	 * @param strResult
	 * @return
	 */
	public Weather getWeather(String strResult) {

		weatherResponse = strResult;
		try {
			String suntime;
			// /解析
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

				if (!weather.weatherD[i].equals("")) {
					if (Integer.parseInt(weather.weatherD[i]) >= 53)
						weather.weatherD[i] = "32";
				}

				weather.weatherN[i] = jsob.getString("fb");
				if (Integer.parseInt(weather.weatherN[i]) >= 53)
					weather.weatherN[i] = "32";

				weather.temperatureD[i] = jsob.getString("fc");
				weather.temperatureN[i] = jsob.getString("fd");
				weather.windDD[i] = jsob.getString("fe");
				weather.windDN[i] = jsob.getString("ff");
				weather.windPD[i] = jsob.getString("fg");
				weather.windPN[i] = jsob.getString("fh");
				weather.suntime[i] = jsob.getString("fi");// 日出日落时间，一会用字符串处理函数分割

			}
			return weather;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getWeatherByBD(String cityid) {

		String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
		String httpArg = "cityid=" + cityid;
		String jsonResult = EncodeUtilByBD.request(httpUrl, httpArg);

		System.out.println("baidu:" + jsonResult);
		return jsonResult;
	}

	public String getTempWeatherByBD(String strResult) {

		try {
			String tempString;
			// String tmpH;
			// /解析
			JSONObject jsonObject;
			// String a = new String(strResult, "UTF-8");
			jsonObject = new JSONObject(strResult);

			JSONObject retData = jsonObject.getJSONObject("retData");
			tempString = retData.getString("temp");
			// tmpH = retData.getString("temp");
			return tempString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取天气指数信息
	 * 
	 * @param strResult
	 * @return
	 */
	public Index getIndex(String strResult) {
		indexResponse = strResult;
		try {
			// /解析
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
	 * 刷新天气指数
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
		i1.setText(myIndex.i_c[0] + ":" + "\n" + myIndex.i_l[0]);
		i2.setText(myIndex.i_c[1] + ":" + "\n" + myIndex.i_l[1]);
		i3.setText(myIndex.i_c[2] + ":" + "\n" + myIndex.i_l[2]);
		
		i3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,SharewearActivity.class);
				startActivity(intent);
				
			}
		});

		// ///////////////////////////////////////////////////////

		i1.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				// new AlertDialog.Builder(MainActivity.this)
				// .setMessage(myIndex.i_5[0])
				// .show();
				getPopupWindowInstance(myIndex.i_5[0], v);
				// popWindow.showAsDropDown(v);
				return false;
			}
		});
		i2.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				getPopupWindowInstance(myIndex.i_5[1], v);
				return false;
			}
		});
		i3.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				getPopupWindowInstance(myIndex.i_5[2], v);
				return false;
			}
		});
	}

	// ////////////////////////////////////////////////////
	public void shareWeather(final String shareMessage) {

		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setType("image/*");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
		// intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	/*
	 * 获取PopupWindow实例
	 */
	private void getPopupWindowInstance(String text, View v) {
		PopuptWindow(text, v);
	}

	/*
	 * 创建PopupWindow
	 */
	private void PopuptWindow(String text, View v) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		/*
		 * LayoutInflater layoutInflater = (LayoutInflater) (MainActivity.this)
		 * .getSystemService(LAYOUT_INFLATER_SERVICE);
		 */
		// 获取自定义布局文件poplayout.xml的视图
		View popview = layoutInflater.inflate(R.layout.i_popview, null);
		popWindow = new PopupWindow(popview, v.getWidth(), v.getWidth(), true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		// 规定弹窗的位置

		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
				location[1] - popWindow.getHeight());
		// PopupWindow里的
		TextView i1pop = (TextView) popview.findViewById(R.id.i1pop);
		i1pop.setText(text);
		/*
		 * i2 = (TextView) popview.findViewById(R.id.i2); i3 = (TextView)
		 * popview.findViewById(R.id.i3);
		 */

		// 创建一个PopupWindow
		// 参数1：contentView 指定PopupWindow的内容
		// 参数2：width 指定PopupWindow的width
		// 参数3：height 指定PopupWindow的height
		// mPopupWindow = new PopupWindow(popupWindow, 100, 130);

		/*
		 * // 获取屏幕和PopupWindow的width和height mScreenWidth =
		 * getWindowManager().getDefaultDisplay().getWidth(); mScreenWidth =
		 * getWindowManager().getDefaultDisplay().getHeight(); mPopupWindowWidth
		 * = mPopupWindow.getWidth(); mPopupWindowHeight =
		 * mPopupWindow.getHeight();
		 */
	}

	/**
	 * 将中文乱码转换为正常编码
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
	 * * 获取指定日后 后 dayAddNum 天的 日期
	 * 
	 * @param day 日期，格式为String："2013-9-3";
	 * 
	 * @param dayAddNum 增加天数 格式为int;
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

	// 实现OnFling方法，就可以利用滑动的起始坐标识别出左右滑动的手势，并处理
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		// 参数e1是按下事件，e2是放开事件，剩下两个是滑动的速度分量，这里用不到
		// 按下时的横坐标大于放开时的横坐标，从右向左滑动
		if (slideMenu.isMenuShow()) {
			slideMenu.hideMenu();
		} else {
			if ((e1.getX() > e2.getX()) && (page < 2)) {

			    Animation lInAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
	            Animation lOutAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            myViewFlipper.setInAnimation(lInAnim);  
	            myViewFlipper.setOutAnimation(lOutAnim); 

				myViewFlipper.showNext();
				page += 1;
				days(page);

			}
			// 按下时的横坐标小于放开时的横坐标，从左向右滑动
			else if ((e1.getX() < e2.getX()) && (page > 0)) {
				
				Animation rInAnim = AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.push_right_in); // 向右滑动左侧进入的渐变效果（alpha 0.1 ->
												// 1.0）
				Animation rOutAnim = AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0 ->
												// 0.1）
				myViewFlipper.setInAnimation(rInAnim);  
				myViewFlipper.setOutAnimation(rOutAnim); 
				
				myViewFlipper.showPrevious();
				page -= 1;
				days(page);
			} else if ((e1.getX() < e2.getX()) && (page == 0)) {
				slideMenu.showMenu();
			}

		}

		return false;
	}

	public void days(int day) {
		String viewdays = getDateStr(day);
		viewday.setText("·" + viewdays);

		String[] daytitles = { "今天", "明天", "后天" };
		daytitle.setText(daytitles[day]);
	}

	/*
	 * 实现OnTouchListener接口中的onTouch()方法，当View上发生触屏时间时调用，传如一个View和一个运动事件event，我们将
	 * 这个event传给OnGestureListener接口的onTouchEvent()方法处理，这样我们的OnFling()就能工作了
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