package com.example.servicemessagertest.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable{
	
	private  int bookId;
	private String bookName;
	
	

	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}
	private Book(Parcel in) {
		bookId = in.readInt();
		bookName = in.readString();
	}

	public Book(int bookId, String bookName) {
		this.bookId = bookId;
		this.bookName = bookName;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(bookId);
		out.writeString(bookName);
	}
	
	public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
		
		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
		
		@Override
		public Book createFromParcel(Parcel in) {
			return new Book(in);
		}
	};
}
