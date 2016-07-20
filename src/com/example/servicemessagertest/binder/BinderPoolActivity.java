package com.example.servicemessagertest.binder;

import com.example.servicemessagertest.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BinderPoolActivity extends AppCompatActivity{
	
	private static final String TAG = "BinderPoolActivity";
	
	BinderPool binderPool;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binderpool);
		
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				doWork();
			}
		}).start();
	}
	
	private void doWork(){
		 binderPool = BinderPool.getInstance(this);
		IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
		ISecurityCenter iSecurityCenter = ISecurityCenterImpl.asInterface(securityBinder);
		Log.i(TAG, "visit IScurityCenter");
		String msg = "helloworld - 安卓";
		System.out.println("content: "+ msg);
		
		try {
			String password = iSecurityCenter.encrypt(msg);
			System.out.println("encrypt: "+ password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, "visit ICompute");
		IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
		ICompute iCompute = IComputeImpl.asInterface(computeBinder);
		try {
			System.out.println("3+5= "+ iCompute.add(3, 5));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
