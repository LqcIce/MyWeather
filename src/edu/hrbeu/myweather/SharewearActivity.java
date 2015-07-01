package edu.hrbeu.myweather;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SharewearActivity extends Activity {

	 String filePath = "C:/Users/Public/Pictures/Sample Pictures/����.jpg";// ����д���ļ�·����ת�����Լ����ļ�·�� 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharewear);
		String myurl = "http://127.0.0.1:8000/media/";
		String uploadFile = Environment.getExternalStorageDirectory()+"/Penguins.jpg";
		String newName = "rsy";
		uploadFile(myurl,uploadFile,newName);
	}
	
	
	public void uploadFile(String actionUrl,String uploadFile,String newName)
    {
      String end ="\r\n";
      String twoHyphens ="--";
      String boundary ="*****";
      
      try
      {
    	//����URL����
        URL url =new URL(actionUrl);
        //����һ��URLConnection��������ʾ��URL�����õ�Զ�̶��������
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        /* ����Input��Output����ʹ��Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        /* ���ô��͵�method=POST */
        con.setRequestMethod("POST");
        /* setRequestProperty */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary="+boundary);
        /* ����DataOutputStream */
        DataOutputStream ds =
          new DataOutputStream(con.getOutputStream());
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; "+
                      "name=\"file1\";filename=\""+
                      newName +"\""+ end);
        ds.writeBytes(end);  
        /* ȡ���ļ���FileInputStream */
        FileInputStream fStream =new FileInputStream(uploadFile);
        /* ����ÿ��д��1024bytes */
        int bufferSize =1024;
        byte[] buffer =new byte[bufferSize];
        int length =-1;
        /* ���ļ���ȡ������������ */
        while((length = fStream.read(buffer)) !=-1)
        {
          /* ������д��DataOutputStream�� */
          ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
        fStream.close();
        ds.flush();
        /* ȡ��Response���� */
        InputStream is = con.getInputStream();
        int ch;
        StringBuffer b =new StringBuffer();
        while( ( ch = is.read() ) !=-1 )
        {
          b.append( (char)ch );
        }
        /* ��Response��ʾ��Dialog */
      //  showDialog("�ϴ��ɹ�"+b.toString().trim());
        /* �ر�DataOutputStream */
        ds.close();
      }
      catch(Exception e)
      {
    //    showDialog("�ϴ�ʧ��"+e);
      }
    }
	
	
	
	
	/*public void getWeatherDate(final String url, final int num,
			final String city, final String citycode) {
		Thread newThread; // ����һ�����߳�
		Log.i("BBBB", "citycode:" + citycode);
		newThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// ����д�����߳���Ҫ���Ĺ���

				Log.i("CCCC", "citycode:" + citycode);

				String uri = "http://127.0.0.1:8000/media/";
				try {
					// ����һ��Ĭ�ϵ�HttpClient
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					
					 final File imageFile = new File(filePath); 
					  MultipartEntity multipartEntity = new MultipartEntity();
					 
					 JSONObject obj = new JSONObject();  
					    obj.put("username", "rsy");  
					    obj.put("headImg", "your parentid");  
					    httppost.setEntity(new StringEntity(obj.toString()));     
					    HttpResponse response;  
					    response = httpclient.execute(httppost);  
					    //����״̬�룬����ɹ���������   
					    int code = response.getStatusLine().getStatusCode();  
					    if (code == 200) {   
					        String rev = EntityUtils.toString(response.getEntity());//����json��ʽ�� {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}          
					        obj = new JSONObject(rev);  
					        String id = obj.getString("username");  
					        String version = obj.getString("headImg");  
					// ����һ��GET����
					HttpGet request = new HttpGet(url);
					Log.v("response text", url);
					// ����GET���󣬲�����Ӧ����ת�����ַ���
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
					myHandler.sendMessage(m);// ������Ϣ:ϵͳ���Զ�����handleMessage������������Ϣ

				}} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		newThread.start(); // �����߳�
	}*/
	
	
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
//				citytitle.setText(myWeather.city);
//				// nowtemp.setText(tempByBD + "��");
//
//				changeview(view_today);
//				RefreshWeather(0);
//				changeview(view_tomorrow);
//				RefreshWeather(1);
//				changeview(view_afterday);
//				RefreshWeather(2);// ���½�����ʾ
			}
			if (msg.what == 2) {
//				RefreshIndex();
			}

			super.handleMessage(msg);

		}
	};
	

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
