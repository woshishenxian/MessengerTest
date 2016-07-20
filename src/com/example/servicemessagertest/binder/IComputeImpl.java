package com.example.servicemessagertest.binder;

import android.os.RemoteException;

public class IComputeImpl extends ICompute.Stub{

	@Override
	public int add(int a, int b) throws RemoteException {
		return a + b;
	}

}
