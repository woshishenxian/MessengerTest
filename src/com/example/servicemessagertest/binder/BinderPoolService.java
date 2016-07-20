package com.example.servicemessagertest.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BinderPoolService extends Service {
	
	private static final String TAG = "BinderPoolService";
	
	private Binder mBindPool = new BinderPool.BinderPoolImpl();

	@Override
	public IBinder onBind(Intent intent) {
		return mBindPool;
	}

}
