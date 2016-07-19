package com.example.servicemessagertest.contentprovider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DbOpenHelper extends SQLiteOpenHelper {
	
	
	

	private static final String DB_NAME = "book_privider.db";
	public static final String BOOK_TABLE_NAME = "book";
	public static final String USER_TABLE_NAME = "user";
	
	private static final int DB_VERSION = 1;
	
	private String create_book_table = "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE_NAME 
			+" (_id INTEGER PRIMARY KEY, name TEXT )";
	
	private String create_user_table ="CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME 
			+ " ( _id INTEGER PRIMARY KEY, name TEXT, sex TEXT)";
	
	

	public DbOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(create_book_table);
		db.execSQL(create_user_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	
}
