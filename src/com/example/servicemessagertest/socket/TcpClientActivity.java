package com.example.servicemessagertest.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.servicemessagertest.R;

public class TcpClientActivity extends AppCompatActivity implements
		OnClickListener {

	private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
	private static final int MESSAGE_SOCKET_CONNECTED = 2;

	private Button mSendButton;
	private TextView mMessageTextView;
	private EditText mMessageEditText;

	private PrintWriter mPrintWriter;
	private Socket mClientSocket;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RECEIVE_NEW_MSG:
				mMessageTextView.setText(mMessageTextView.getText()
						+ (String) msg.obj);
				break;
			case MESSAGE_SOCKET_CONNECTED:
				mSendButton.setEnabled(true);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcpclient);

		mSendButton = (Button) findViewById(R.id.mSendButton);
		mMessageEditText = (EditText) findViewById(R.id.mMessageEditText);
		mMessageTextView = (TextView) findViewById(R.id.mMessageTextView);

		mSendButton.setOnClickListener(this);
		Intent service = new Intent(this, TcpServerService.class);
		startService(service);
		new Thread() {

			@Override
			public void run() {
				connectTcpServer();
			}

		}.start();
	}
	

	@Override
	protected void onDestroy() {
		if (mClientSocket != null) {
			try {
				mClientSocket.shutdownInput();
				mClientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if(v == mSendButton){
			final String msg = mMessageEditText.getText().toString();
			if(!TextUtils.isEmpty(msg) && mPrintWriter !=null){
				mPrintWriter.println(msg);
				mMessageEditText.setText("");
				String time = formatDataTime(System.currentTimeMillis());
				final String showedMsg = "self " + time + ":" + msg +"\n";
				mMessageTextView.setText(mMessageTextView.getText() +showedMsg);
			}
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String formatDataTime(long time){
		return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
	}
	
	private void connectTcpServer(){
		Socket socket = null;
		while (socket == null) {
			try {
				socket = new Socket("localhost", 8688);
				mClientSocket = socket;
				mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
				System.out.println("connect server success");
			} catch (IOException e) {
				SystemClock.sleep(1000);
				System.out.println("connect tcp server fialed , retry ....");
			}
			
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (!TcpClientActivity.this.isFinishing()) {
				String msg = br.readLine();
				System.out.println("receive: " + msg);
				if(msg !=null){
					String time = formatDataTime(System.currentTimeMillis());
					final String showMsg = "server" + time + ":" + msg + "\n";
					mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,showMsg).sendToTarget();
				}
			}
			System.out.println("quit .....");
			mPrintWriter.close();
			br.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
