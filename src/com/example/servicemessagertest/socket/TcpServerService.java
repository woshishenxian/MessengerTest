package com.example.servicemessagertest.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TcpServerService extends Service {

	private boolean mIsServiceDestoryed = false;
	private String[] mDefinedMessages = new String[] { "你好啊，哈哈", "请问你叫什么名字",
			"北京天气不错啊，shy", "你知道吗？我可是可以和多个人同时聊天的敖",
			"给你讲个笑话吧，据说爱笑的人运气不回太差，不知道真假。" };

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(new TcpService()).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mIsServiceDestoryed = true;
	}
	
	private class TcpService implements Runnable{

		@SuppressWarnings("resource")
		@Override
		public void run() {
			ServerSocket serverSocket = null;
			try {
				//监听本地8688端口
				serverSocket = new ServerSocket(8688);
			} catch (IOException e) {
				System.err.print("establish tcp server failed, port : 8688");
				e.printStackTrace();
				return;
			}
			
			while (!mIsServiceDestoryed) {
				try {
					//接受客户端请求
					final Socket client = serverSocket.accept();
					System.out.println("accept");
					new Thread(){

						@Override
						public void run() {
							try {
								responseClient(client);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void responseClient(Socket client) throws IOException{
		//用于接受客户端消息
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		//用于向客户端发送消息
		PrintWriter out  = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
		out.println("欢迎来到聊天室");
		while(!mIsServiceDestoryed){
			String str = in.readLine();
			System.out.println("msg from client : " + str);
			if(str == null){
				//客户端断开连接
				out.println("onnect tcp server breaked");
				break;
			}
			int i = new Random().nextInt(mDefinedMessages.length);
			String msg = mDefinedMessages[i];
			out.println(msg);
			System.out.println("send : "+msg);
		}
		System.out.println("client quit.");
		//关闭流
		out.close();
		in.close();
		client.close();
	}

}
