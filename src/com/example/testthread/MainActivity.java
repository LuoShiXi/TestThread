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

		final Handler handler = new Handler(){//用来进行线程间通信
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				tv.append("哈");
			}
		};

		Thread t = new Thread(new Runnable() {
			public void run() {

				for(int i = 0; i < 100; i++){

					//					tv.append("哈哈");

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
			public void handleMessage(Message msg) {//接收子线程的消息
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				if(msg.what == 0){//代表循环还没有执行完成
					pb.setProgress(msg.arg1);
				}else{
					Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
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
					//					以上几个参数都是int类型
					//					msg.obj  这个参数可以发送任意类型的数据
					msg.what = 0;//0代表循环正在执行
					msg.arg1 = i;//arg1用来发送int数值
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
		p.name = "张三";
		p.age = 20;
		p.num = "10086";

		Gson gson = new Gson();

		//使用Gson包将对象转换成json格式数据
		//		String data = gson.toJson(p);
		//		
		//		tv.setText(data);


		//使用gson包将数组转换成json格式数据
		Person p1 = new Person();
		p1.name = "李四";
		p1.age = 10;
		p1.num = "123456";

		Person p2 = new Person();
		p2.name = "王五";
		p2.age = 15;
		p2.num = "112233";

		Person[] ps = {p, p1, p2};

		String data = gson.toJson(ps);
		tv.setText(data);

		//		在json格式的数据中，{}圈起来的代表是一个对象，[]圈起来的代表是一个数组或是集合

		//		[
		//		 
		//		 {"name":"张三","age":20,"num":"10086"},
		//		 {},
		//		 {}
		//		 
		//		 ]


	}

	public void toObj(View v){

		String data = tv.getText().toString();//获取json格式的数据  对象

		Gson gson = new Gson();

		Person p = gson.fromJson(data, Person.class);//第一个参数是要解析的数据     第二个参数是要转换的类型

		Toast.makeText(MainActivity.this, p.name + p.age + p.num, Toast.LENGTH_SHORT).show();

	}


	public void toArray(View v){

		String data = tv.getText().toString();//获取json格式的数据  数组

		Gson gson = new Gson();
		//		 new TypeToken<Person[]>(){}.getType()
		List<Person> list = gson.fromJson(data, new TypeToken<List<Person>>(){}.getType());//第一个参数是要解析的数据     第二个参数是要转换的类型

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
				String line = null;//用来接收返回数据
				BufferedReader buffer = null;//用来转换字节输入流
				
				String str_url = "http://weather.123.duba.net/static/weather_info/101010100.html";

				try {
					URL url = new URL(str_url);//将请求地址封装到URL对象当中

					HttpURLConnection con = (HttpURLConnection) url.openConnection();//打开http请求链接

					con.setConnectTimeout(10000);//设置链接超时时间
					con.setDoInput(true);//允许url链接进行输入
					con.setRequestMethod("GET");//设置请求方式为get请求
					
					int response_code = con.getResponseCode();//获取相应代码
					
					if(response_code == HttpURLConnection.HTTP_OK){//200  代表链接成功
						
						InputStream is = con.getInputStream();//打开输入流获取返回的数据   字节输入流
						
						buffer = new BufferedReader(new InputStreamReader(is));//把字节输入流转换成字符输入流   把字符输入流转换成字符缓冲流
						
						while((line = buffer.readLine()) != null){//读取数据  从流中读取一行数据并赋值给line  判断line是否为null
							sb.append(line);//将每次读 取的一行数据追加到stringbuffer当中
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
