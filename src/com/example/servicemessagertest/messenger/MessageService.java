package com.example.servicemessagertest.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessageService extends Service {
	
	private static final String TAG = "MessagerService";
	
	private static class MessengerHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyConstants.MSG_FROM_CLIENT:
				Log.i(TAG, "receive msg from client:" + msg.getData().getString("msg"));
				Messenger messenger = msg.replyTo;
				Message message = Message.obtain(null, MyConstants.MSG_FROM_SERVICE);
				Bundle bundle = new Bundle();
				bundle.putString("reply", "消息已收到，稍后恢复");
				message.setData(bundle);
				try {
					messenger.send(message);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}
	
	private final Messenger mMessenger = new Messenger(new MessengerHandler());

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mMessenger.getBinder();
	}

}
