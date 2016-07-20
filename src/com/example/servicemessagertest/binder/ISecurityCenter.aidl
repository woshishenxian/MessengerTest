package com.example.servicemessagertest.binder;

interface ISecurityCenter {
	String encrypt(String content);
	String decrypt(String password);
}