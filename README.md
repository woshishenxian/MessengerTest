# MessengerTest
Android系统中使用Messenger进行进程间通信

我们使用Handler都是在一个进程中使用的，如何跨进程使用Handler？

其实这个问题不难解决，自己动手对binder进行一些封装就可以简单实现。但是当你看系统源码，就会发现，其实这些android都已经为我们做好了。

使用android系统的android.os.Messenger可以很方便的跨进程使用Handler。下面是示例程序。

private static class MessengerHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyConstants.MSG_FROM_CLIENT:
				Log.i(TAG, "receive msg from client:" + msg.getData().getString("msg"));
				Messenger messenger = msg.replyTo;
				Message message = Message.obtain(null, MyConstants.MSG_FROM_SERVICE);
				Bundle bundle = new Bundle();
				bundle.putString("reply", "消息已收到，稍后恢复");
				message.setData(bundle);
				try {
					messenger.send(message);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

然后是客户端

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MessengerActivity";

	private Messenger mService;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = new Messenger(service);
			Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
			Bundle data = new Bundle();
			data.putString("msg", "hello , this is client.");
			msg.setData(data);
			msg.replyTo = messenger;
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private Messenger messenger = new Messenger(new MessengerHandler());

	private static class MessengerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyConstants.MSG_FROM_SERVICE:
				Log.i(TAG, "receive msg from Service: " + msg.getData().getString("reply"));
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(this, MessageService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
	}

}


返回博客列表
原 Android中Messenger的使用
cwr  cwr
发布时间: 2015/02/12 22:48 阅读: 4366 收藏: 5 点赞: 0 评论: 1
摘要
Android系统中使用Messenger进行进程间通信
我们使用Handler都是在一个进程中使用的，如何跨进程使用Handler？

其实这个问题不难解决，自己动手对binder进行一些封装就可以简单实现。但是当你看系统源码，就会发现，其实这些android都已经为我们做好了。

使用android系统的android.os.Messenger可以很方便的跨进程使用Handler。下面是示例程序。



首先是服务端。

public class MessengerTestService extends Service {
 
    protected static final String TAG = "MessengerTestService";
     
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
                Log.d(TAG, "收到消息");
                 //获取客户端message中的Messenger，用于回调
                final Messenger callback = msg.replyTo;
                try {
                    // 回调
                    callback.send(Message.obtain(null, 0));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
        }
    };
     
    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(mHandler).getBinder();
    }
 
}




然后是客户端

public class MainActivity extends Activity {
 
    protected static final String TAG = "MainActivity";
    Messenger messenger;
    Messenger reply;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reply = new Messenger(handler);
        Intent intent = new Intent();
        intent.setClassName("test.messenger", "test.messenger.MessengerTestService");
         
        // 绑定服务
        bindService(intent, new ServiceConnection() {
             
            @Override
            public void onServiceDisconnected(ComponentName name) {
                 
            }
             
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Toast.makeText(MainActivity.this, "bind success", 0).show();
                messenger = new Messenger(service);
            }
        }, Context.BIND_AUTO_CREATE);
         
    }
 
    public void sendMessage(View v) {
        Message msg = Message.obtain(null, 1);
        // 设置回调用的Messenger
        msg.replyTo = reply;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
     
    private Handler handler = new Handler() {
 
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "回调成功");
        }
         
    };
}


客户端绑定服务端，获取远程Messenger的binder对象。调用Messenger的send函数，就可以吧Message发送至服务端的Handler。

同时，如果需要服务端回调客户端（往客户端的Handler发消息），则可以在send的Message中设置replyTo，服务端就可以往客户端发送消息了。
