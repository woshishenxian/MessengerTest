package com.example.servicemessagertest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MessengerActivity";

	private Messenger mService;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = new Messenger(service);
			Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
			Bundle data = new Bundle();
			data.putString("msg", "hello , this is client.");
			msg.setData(data);
			msg.replyTo = messenger;
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private Messenger messenger = new Messenger(new MessengerHandler());

	private static class MessengerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyConstants.MSG_FROM_SERVICE:
				Log.i(TAG, "receive msg from Service: " + msg.getData().getString("reply"));
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(this, MessageService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
	}

}
