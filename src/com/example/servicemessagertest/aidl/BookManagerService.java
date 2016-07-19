package com.example.servicemessagertest.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerService extends Service {

	private static final String TAG = "BMS";

	private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
	
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();

	private Binder mBinder = new IBookManager.Stub() {

		@Override
		public List<Book> getBookList() throws RemoteException {
			return mBookList;
		}

		@Override
		public void addBook(Book book) throws RemoteException {
			mBookList.add(book);
		}

		@Override
		public void registerListener(IOnNewBookArrivedListener mIOnNewBookArrivedListener)
				throws RemoteException {
			if(!mListenerList.contains(mIOnNewBookArrivedListener)){
				mListenerList.add(mIOnNewBookArrivedListener);
			}else{
				Log.d(TAG, "already exists.");
			}
			Log.d(TAG, "registerListener , size: "+mListenerList.size());
		}

		@Override
		public void unregisterListener(IOnNewBookArrivedListener mIOnNewBookArrivedListener)
				throws RemoteException {
			if(mListenerList.contains(mIOnNewBookArrivedListener)){
				mListenerList.remove(mIOnNewBookArrivedListener);
				Log.d(TAG, "unregister listener succeed.");
			}else {
				Log.d(TAG, "not found,can not unregister.");
			}
			Log.d(TAG, "unregisterListener ,current size: "+mListenerList.size());
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mBookList.add(new Book(1, "Android"));
		mBookList.add(new Book(2, "IOS"));
		new Thread(new ServiceWorker()).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		mIsServiceDestoryed.set(true);
		super.onDestroy();
	}
	
	private void onNewBookArrived(Book book) throws RemoteException{
		mBookList.add(book);
		Log.d(TAG, "onNewBookArrived, notify liseners:" + mListenerList.size());
		for (IOnNewBookArrivedListener l : mListenerList) {
			Log.d(TAG, "onNewBookArrived, notify listener ; " + l);
			l.onNewBookArrived(book);
		}
	}

	private class ServiceWorker implements Runnable{

		@Override
		public void run() {
			while(!mIsServiceDestoryed.get()){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int bookId = mBookList.size()+1;
				Book newBook = new Book(bookId, "new book#" + bookId);
				try {
					onNewBookArrived(newBook);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
