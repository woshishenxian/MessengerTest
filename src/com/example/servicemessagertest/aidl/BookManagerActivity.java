package com.example.servicemessagertest.aidl;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.servicemessagertest.R;

public class BookManagerActivity extends AppCompatActivity {

	private static final String TAG = "BookManagerActivity";
	private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

	private IBookManager mRemoteBookManager;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_NEW_BOOK_ARRIVED:
				Log.i(TAG, "receive new book: " + msg.obj);
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmanager);

		Intent intent = new Intent(this, BookManagerService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteBookManager = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IBookManager bookManager = IBookManager.Stub.asInterface(service);
			try {
				mRemoteBookManager = bookManager;
				List<Book> list = bookManager.getBookList();
				Log.i(TAG, "query book list: " + list.toString());
				bookManager.addBook(new Book(3, "android开发艺术探索"));
				List<Book> newList = bookManager.getBookList();
				Log.i(TAG, "query book list , list type: "
						+ list.getClass().getCanonicalName());
				Log.i(TAG, "query book list: " + newList.toString());
				mRemoteBookManager.registerListener(mIOnNewBookArrivedListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

		@Override
		public void onNewBookArrived(Book newBook) throws RemoteException {
			mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook)
					.sendToTarget();
		}
	};

	@Override
	protected void onDestroy() {
		if(mRemoteBookManager !=null && mRemoteBookManager.asBinder().isBinderAlive()){
			try {
				Log.i(TAG, "unregister listener : "+ mIOnNewBookArrivedListener);
				mRemoteBookManager.unregisterListener(mIOnNewBookArrivedListener);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		unbindService(connection);
		super.onDestroy();
	}

}
