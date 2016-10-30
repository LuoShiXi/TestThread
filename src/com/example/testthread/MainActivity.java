package com.example.testthread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {


	//	http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=101010100
	//		http://weather.123.duba.net/static/weather_info/101010100.html

	TextView tv;
	ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv = (TextView) findViewById(R.id.tv);
		pb = (ProgressBar) findViewById(R.id.progressBar1);

	}

	public void start(View v){

		final Handler handler = new Handler(){//���������̼߳�ͨ��
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				tv.append("��");
			}
		};

		Thread t = new Thread(new Runnable() {
			public void run() {

				for(int i = 0; i < 100; i++){

					//					tv.append("����");

					handler.sendEmptyMessage(0);

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});
		t.start();


	}

	public void download(View v){

		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {//�������̵߳���Ϣ
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				if(msg.what == 0){//����ѭ����û��ִ�����
					pb.setProgress(msg.arg1);
				}else{
					Toast.makeText(MainActivity.this, "�������", Toast.LENGTH_SHORT).show();
				}
			}
		};

		Thread t = new Thread(new Runnable() {
			public void run() {

				for(int i = 1; i < 101; i++){

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Message msg = Message.obtain();
					//					msg.what
					//					msg.arg1
					//					msg.arg2
					//					���ϼ�����������int����
					//					msg.obj  ����������Է����������͵�����
					msg.what = 0;//0����ѭ������ִ��
					msg.arg1 = i;//arg1��������int��ֵ
					handler.sendMessage(msg);
				}

				Message msg = Message.obtain();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		});
		t.start();
	}

	public void toJson(View v){

		Person p = new Person();
		p.name = "����";
		p.age = 20;
		p.num = "10086";

		Gson gson = new Gson();

		//ʹ��Gson��������ת����json��ʽ����
		//		String data = gson.toJson(p);
		//		
		//		tv.setText(data);


		//ʹ��gson��������ת����json��ʽ����
		Person p1 = new Person();
		p1.name = "����";
		p1.age = 10;
		p1.num = "123456";

		Person p2 = new Person();
		p2.name = "����";
		p2.age = 15;
		p2.num = "112233";

		Person[] ps = {p, p1, p2};

		String data = gson.toJson(ps);
		tv.setText(data);

		//		��json��ʽ�������У�{}Ȧ�����Ĵ�����һ������[]Ȧ�����Ĵ�����һ��������Ǽ���

		//		[
		//		 
		//		 {"name":"����","age":20,"num":"10086"},
		//		 {},
		//		 {}
		//		 
		//		 ]


	}

	public void toObj(View v){

		String data = tv.getText().toString();//��ȡjson��ʽ������  ����

		Gson gson = new Gson();

		Person p = gson.fromJson(data, Person.class);//��һ��������Ҫ����������     �ڶ���������Ҫת��������

		Toast.makeText(MainActivity.this, p.name + p.age + p.num, Toast.LENGTH_SHORT).show();

	}


	public void toArray(View v){

		String data = tv.getText().toString();//��ȡjson��ʽ������  ����

		Gson gson = new Gson();
		//		 new TypeToken<Person[]>(){}.getType()
		List<Person> list = gson.fromJson(data, new TypeToken<List<Person>>(){}.getType());//��һ��������Ҫ����������     �ڶ���������Ҫת��������

		for(Person p : list){
			System.out.println(p.name + "==" + p.age + "==" + p.num);
		}

	}



	public void doGet(View v){
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				String data = msg.obj.toString();
				
				tv.setText(data);
			}
		};
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub


				StringBuffer sb = new StringBuffer();
				String line = null;//�������շ�������
				BufferedReader buffer = null;//����ת���ֽ�������
				
				String str_url = "http://weather.123.duba.net/static/weather_info/101010100.html";

				try {
					URL url = new URL(str_url);//�������ַ��װ��URL������

					HttpURLConnection con = (HttpURLConnection) url.openConnection();//��http��������

					con.setConnectTimeout(10000);//�������ӳ�ʱʱ��
					con.setDoInput(true);//����url���ӽ�������
					con.setRequestMethod("GET");//��������ʽΪget����
					
					int response_code = con.getResponseCode();//��ȡ��Ӧ����
					
					if(response_code == HttpURLConnection.HTTP_OK){//200  �������ӳɹ�
						
						InputStream is = con.getInputStream();//����������ȡ���ص�����   �ֽ�������
						
						buffer = new BufferedReader(new InputStreamReader(is));//���ֽ�������ת�����ַ�������   ���ַ�������ת�����ַ�������
						
						while((line = buffer.readLine()) != null){//��ȡ����  �����ж�ȡһ�����ݲ���ֵ��line  �ж�line�Ƿ�Ϊnull
							sb.append(line);//��ÿ�ζ� ȡ��һ������׷�ӵ�stringbuffer����
						}
					}
					
					String data = sb.toString();
					
					Message msg = Message.obtain();
					msg.obj = data;
					handler.sendMessage(msg);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					if(buffer != null){
						try {
							buffer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		t.start();


	}









}
