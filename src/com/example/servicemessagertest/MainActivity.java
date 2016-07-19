package com.example.servicemessagertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.servicemessagertest.aidl.BookManagerActivity;
import com.example.servicemessagertest.messenger.MessengerActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void turnToMessengerActivity(View view){
		startActivity(new Intent(this, MessengerActivity.class));
	}
	public void turnToBookManagerActivity(View view){
		startActivity(new Intent(this, BookManagerActivity.class));
	}

}
