package com.example.servicemessagertest.binder;

import java.util.concurrent.CountDownLatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class BinderPool {

	private static final String TAG = "BinderPool";
	private static final int BINDER_NONE = -1;
	public static final int BINDER_COMPUTE = 0;
	public static final int BINDER_SECURITY_CENTER = 1;

	private Context mContext;
	private IBinderPool mBinderPool;
	// 用volatile修饰的变量，线程在每次使用变量的时候，都会读取变量修改后的值。volatile很容易被误用，用来进行原子性操作。
	private static volatile BinderPool mInstance;
	// 一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
	private CountDownLatch mConnectBinderPoolCountDownLatch;

	private BinderPool(Context mContext) {
		this.mContext = mContext.getApplicationContext();
		connectBindPoolService();
	}

	public static BinderPool getInstance(Context mContext) {
		if (mInstance == null) {
			synchronized (BinderPool.class) {
				if (mInstance == null) {
					mInstance = new BinderPool(mContext);
				}
			}
		}
		return mInstance;
	}

	private synchronized void connectBindPoolService() {
		mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
		Intent service = new Intent(mContext, BinderPoolService.class);
		mContext.bindService(service, mBinderPoolConnection,
				Context.BIND_AUTO_CREATE);
		try {
			// 调用此方法会一直阻塞当前线程，直到计时器的值为0
			mConnectBinderPoolCountDownLatch.await(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IBinder queryBinder(int binderCode) {
		IBinder binder = null;
		try {
			if (mBinderPool != null) {
				binder = mBinderPool.queryBinder(binderCode);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return binder;
	}

	private ServiceConnection mBinderPoolConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinderPool = IBinderPool.Stub.asInterface(service);
			try {
				mBinderPool.asBinder().linkToDeath(recipient, 0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			// 当前线程调用此方法，则计数减一
			mConnectBinderPoolCountDownLatch.countDown();
		}
	};

	private IBinder.DeathRecipient recipient = new IBinder.DeathRecipient() {

		@Override
		public void binderDied() {
			mBinderPool.asBinder().unlinkToDeath(recipient, 0);
			mBinderPool = null;
			connectBindPoolService();
		}
	};
	
	public static class BinderPoolImpl extends IBinderPool.Stub{

		
		
		public BinderPoolImpl() {
			super();
		}

		@Override
		public IBinder queryBinder(int binderCode) throws RemoteException {
			IBinder binder = null;
			switch (binderCode) {
			case BINDER_SECURITY_CENTER:
				binder = new ISecurityCenterImpl();
				break;
			case BINDER_COMPUTE:
				binder = new IComputeImpl();
				break;
			default:
				break;
			}
			return binder;
		}

		
	}
}
