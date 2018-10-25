package com.onepiggy.chat.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.onepiggy.chat.R;

public class ChatActivity extends Activity implements OnClickListener{
	
	private Button[] buttons = new Button[9];
	private Button btNum1,btNum2,btNum3,btNum4,btNum5,btNum6,btNum7,btNum8,btNum9;
	private Button btLink, btSend;
	private EditText etIp, etMessage;
	private TextView tvReceived;
	private Socket socket;
	private OutputStream os;
	private PrintWriter pw;
	private InputStream is;
	private BufferedReader br;
	private ScrollView svReceived;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		initView();
		
		setupListener();
	}

	private void setupListener() {
		btNum1.setOnClickListener(this);
		btNum2.setOnClickListener(this);
		btNum3.setOnClickListener(this);
		btNum4.setOnClickListener(this);
		btNum5.setOnClickListener(this);
		btNum6.setOnClickListener(this);
		btNum7.setOnClickListener(this);
		btNum8.setOnClickListener(this);
		btNum9.setOnClickListener(this);
		
		btLink.setOnClickListener(this);
		btSend.setOnClickListener(this);
		
	}

	private void initView() {
		btNum1 = (Button) findViewById(R.id.bt_num_1);
		btNum2 = (Button) findViewById(R.id.bt_num_2);
		btNum3 = (Button) findViewById(R.id.bt_num_3);
		btNum4 = (Button) findViewById(R.id.bt_num_4);
		btNum5 = (Button) findViewById(R.id.bt_num_5);
		btNum6 = (Button) findViewById(R.id.bt_num_6);
		btNum7 = (Button) findViewById(R.id.bt_num_7);
		btNum8 = (Button) findViewById(R.id.bt_num_8);
		btNum9 = (Button) findViewById(R.id.bt_num_9);
		
		buttons[0] = btNum1;
		buttons[1] = btNum2;
		buttons[2] = btNum3;
		buttons[3] = btNum4;
		buttons[4] = btNum5;
		buttons[5] = btNum6;
		buttons[6] = btNum7;
		buttons[7] = btNum8;
		buttons[8] = btNum9;
		
		btLink = (Button) findViewById(R.id.bt_link);
		btSend = (Button) findViewById(R.id.bt_send);
		
		etIp = (EditText) findViewById(R.id.et_ip);
		etMessage = (EditText) findViewById(R.id.et_message);
		
		tvReceived = (TextView) findViewById(R.id.tv_received);
		svReceived = (ScrollView) findViewById(R.id.sv_text);
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		closeSocket();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_link:
			socketLink(etIp.getText().toString());
			break;
		case R.id.bt_num_1:
			sendMessage("1");	
			break;
		case R.id.bt_num_2:
			sendMessage("2");
			break;
		case R.id.bt_num_3:
			sendMessage("3");
			break;
		case R.id.bt_num_4:
			sendMessage("4");
			break;
		case R.id.bt_num_5:
			sendMessage("5");
			break;
		case R.id.bt_num_6:
			sendMessage("6");
			break;
		case R.id.bt_num_7:
			sendMessage("7");
			break;
		case R.id.bt_num_8:
			sendMessage("8");
			break;
		case R.id.bt_num_9:
			sendMessage("9");
			break;
		case R.id.bt_send:
			sendMessage(etMessage.getText().toString());
			etMessage.setText("");
			break;
		default:
			break;
		}
	}

	private void socketLink(String ip) {
		if(TextUtils.isEmpty(ip)) {
			showToast("ip为空！");
			return;
		}
		
		String[] splits = ip.split(":");
		if(splits.length < 2) {
			showToast("没有端口号！");
			return;
		}
		
		final String newIp = splits[0];
		final String port = splits[1];
		
		new Thread() {
			

			public void run() {
				try {
					socket = new Socket(newIp, Integer.parseInt(port));
					os = socket.getOutputStream();//字节输出流
					pw =new PrintWriter(os);//将输出流包装成打印流
					
					is = socket.getInputStream();
					if(is != null) {
						updateMessage(newIp+ ":" + port + " 连接成功！"); 
						handler.sendEmptyMessage(1);
					}
					br = new BufferedReader(new InputStreamReader(is));
					String info = null;
					while((info=br.readLine()) != null){
						updateMessage(info);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	
	private void sendMessage(final String message){
		new Thread() {
			@Override
			public void run() {
				if(pw != null) {
					pw.println(message);
					pw.flush();
				}
			}
		}.start();
	};
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				tvReceived.setText(tvReceived.getText() + "\n" + (String) msg.obj);
				svReceived.fullScroll(View.FOCUS_DOWN);
				break;
			case 1:
				btLink.setEnabled(false);
				break;
			default:
				break;
			}
		};
	};
	
	private void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
	
	
	private void updateMessage(String info) {
		handler.sendMessage(handler.obtainMessage(0, info));
	}

	
	private void closeSocket(){
		try {
			pw.close();
			os.close();
			is.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	};
	
	
}
