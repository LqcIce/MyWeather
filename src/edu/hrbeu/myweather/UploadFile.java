package edu.hrbeu.myweather;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFile {

//    private String newName ="image.jpg";
//    private String uploadFile ="/sdcard/image.JPG";
//    private String actionUrl ="http://192.168.0.71:8086/HelloWord/myForm";
//	
	
	/* �ϴ��ļ���Server�ķ��� */
	public void uploadFile(String actionUrl,String uploadFile,String newName)
    {
      String end ="\r\n";
      String twoHyphens ="--";
      String boundary ="*****";
      try
      {
        URL url =new URL(actionUrl);
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


    
}