package comefindme.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SMSService extends Service {

	private final IBinder mBinder = new LocalBinder();

	private Thread smsThread;
	private List<SMSListener> smsListenerList = new ArrayList<SMSListener>();

	public void addSMSListener(SMSListener smsListener) {
		synchronized (this.smsListenerList) {
			this.smsListenerList.add(smsListener);
		}
	}

	public void removeSMSListener(SMSListener smsListener) {
		synchronized (this.smsListenerList) {
			this.smsListenerList.remove(smsListener);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			String object = (String) msg.getData().get("values");
			String[] values = object.split(",");
			int x = Integer.valueOf(values[0]);
			int y = Integer.valueOf(values[1]);
			int z = Integer.valueOf(values[2]);

			synchronized (smsListenerList) {
				for (SMSListener listener : smsListenerList) {
					listener.handleSMSValues(x, y, z);
				}
			}

			super.handleMessage(msg);
		}
	};

	Runnable smsRunnable = new Runnable() {
		public void run() {
			Socket socket = null;
			BufferedReader in = null;
			try {
				socket = new Socket("10.0.2.2", 4444);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (UnknownHostException e) {
				Log.d("SMSService", "Unknown Host.", e);
			} catch (IOException e) {
				Log.d("SMSService", "Couldn't get I/O for the connection to: localhost.", e);
			}
			String fromServer;

			try {
				while ((fromServer = in.readLine()) != null) {
					Message msg = new Message();
					HashMap map = new HashMap();
					map.put("values", fromServer);
					msg.setData(map);
					handler.sendMessage(msg);
				}
				in.close();
				socket.close();
			} catch (IOException e) {
				Log.d("SMSService", "Couldn't get I/O for the connection to: localhost.", e);
			}
		}
	};

	@Override
	protected void onCreate() {
		super.onCreate();
		this.smsThread = new Thread(smsRunnable);
		this.smsThread.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		SMSService getService() {
			return SMSService.this;
		}
	}

	public interface SMSListener {
		public void handleSMSValues(int x, int y, int z);
	}

}
