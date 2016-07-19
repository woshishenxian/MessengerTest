package com.example.servicemessagertest.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerService extends Service {

	private static final String TAG = "BMS";

	private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
	
	//不提供跨进程移除操作
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
	//多次跨进程传输客户端的统一对象会在服务端生成不同的对象，但是这些新生成的对象有同一个共同点，
	//那就是它们底层的binder对象是同一个。
	//RemoteCallbackList 是系统义工的用于删除跨进程listener的接口，RemoteCallbackList是一个范型，支持管理任意的AIDL接口。
//	private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();
	private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();

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
			mListenerList.register(mIOnNewBookArrivedListener);
//			if(!mListenerList.contains(mIOnNewBookArrivedListener)){
//				mListenerList.add(mIOnNewBookArrivedListener);
//			}else{
//				Log.d(TAG, "already exists.");
//			}
//			Log.d(TAG, "registerListener , size: "+mListenerList.size());
		}

		@Override
		public void unregisterListener(IOnNewBookArrivedListener mIOnNewBookArrivedListener)
				throws RemoteException {
			mListenerList.unregister(mIOnNewBookArrivedListener);
//			if(mListenerList.contains(mIOnNewBookArrivedListener)){
//				mListenerList.remove(mIOnNewBookArrivedListener);
//				Log.d(TAG, "unregister listener succeed.");
//			}else {
//				Log.d(TAG, "not found,can not unregister.");
//			}
//			Log.d(TAG, "unregisterListener ,current size: "+mListenerList.size());
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
		//遍历RemoteCallbackList 必须要按照下面的方式进行，其中beginBroadcast 和 finishBroadcast
		//必须要配对使用，哪怕是仅仅要获取RemoteCallbackList 的元素个数
		final int N = mListenerList.beginBroadcast();
		for (int i = 0; i < N; i++) {
			IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
			if(l !=null){
				l.onNewBookArrived(book);
			}
		}
		mListenerList.finishBroadcast();
//		Log.d(TAG, "onNewBookArrived, notify liseners:" + mListenerList.size());
//		for (IOnNewBookArrivedListener l : mListenerList) {
//			Log.d(TAG, "onNewBookArrived, notify listener ; " + l);
//			l.onNewBookArrived(book);
//		}
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
