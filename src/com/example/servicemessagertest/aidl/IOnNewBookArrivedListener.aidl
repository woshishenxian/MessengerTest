package com.example.servicemessagertest.aidl;

import com.example.servicemessagertest.aidl.Book;

interface IOnNewBookArrivedListener{
	void onNewBookArrived(in Book newBook);
}