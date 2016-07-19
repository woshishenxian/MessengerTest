package com.example.servicemessagertest.aidl;

import com.example.servicemessagertest.aidl.Book;
import com.example.servicemessagertest.aidl.IOnNewBookArrivedListener;

interface IBookManager{
	List<Book> getBookList();
	void addBook(in Book book);
	void registerListener(IOnNewBookArrivedListener mIOnNewBookArrivedListener);
	void unregisterListener(IOnNewBookArrivedListener mIOnNewBookArrivedListener);
}