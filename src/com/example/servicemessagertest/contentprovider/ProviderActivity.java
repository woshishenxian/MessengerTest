package com.example.servicemessagertest.contentprovider;

import com.example.servicemessagertest.R;
import com.example.servicemessagertest.aidl.Book;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class ProviderActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provideer);
		
		ContentValues values = new ContentValues();
		values.put("_id", 6);
		values.put("name", "程序设计的艺术");
		getContentResolver().insert(BookProvider.BOOK_CONTENT_URI, values);
		
		Cursor bCursor= getContentResolver().query(BookProvider.BOOK_CONTENT_URI, new String[]{"_id" , "name"}, null, null, null);
		while(bCursor.moveToNext()){
			Book book = new Book();
			book.setBookId(bCursor.getInt(0));
			book.setBookName(bCursor.getString(1));
		}
		bCursor.close();
	}

}
