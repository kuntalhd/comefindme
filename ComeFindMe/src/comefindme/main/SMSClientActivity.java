package comefindme.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;

public class SMSClientActivity extends Activity {

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	private SMSView myView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		this.myView = new SMSView(this);
		setContentView(this.myView);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.add(0, 1, "Start");
		menu.add(0, 2, "Clear");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(Menu.Item item) {
		if (item.getId() == 1) {
			Thread thread = new Thread(smsThread);
			thread.start();
		} else if (item.getId() == 2) {
			myView.clear();
		}
		return super.onOptionsItemSelected(item);
	}

	Handler handler = new Handler() {
		private boolean start = true;

		@Override
		public void handleMessage(Message msg) {

			String object = (String) msg.getData().get("values");
			// Log.d("[TAG]", object);

			String[] values = object.split(",");
			float x1 = Float.valueOf(values[0]);
			float y1 = Float.valueOf(values[1]);

			float x = x1 * -1;
			float y = y1 * -1;

			if (start) {

				myView.start(x, y);
				start = false;
			} else {
				myView.move(x, y);
			}
			super.handleMessage(msg);
		}
	};

	Runnable smsThread = new Runnable() {
		public void run() {
			Socket kkSocket = null;
			PrintWriter out = null;
			BufferedReader in = null;

			try {

				kkSocket = new Socket("10.0.2.2", 4444);
				// out = new PrintWriter(kkSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			} catch (UnknownHostException e) {
				// System.err.println("Don't know about host: taranis.");
				// System.exit(1);
				Log.d("[SMSTAG]", "Unknown Host.", e);
			} catch (IOException e) {
				// System.err.println("Couldn't get I/O for the connection to:
				// taranis.");
				// System.exit(1);
				Log.d("[SMSTAG]", "Couldn't get I/O for the connection to: localhost.", e);
			}
			String fromServer;

			try {
				while ((fromServer = in.readLine()) != null) {
					// textView.append(fromServer);
					// Log.d("[SMSTAG]", fromServer);
					Message msg = new Message();
					HashMap map = new HashMap();
					map.put("values", fromServer);
					msg.setData(map);
					handler.sendMessage(msg);

					// if (fromServer.equals("Bye."))
					// break;
					//
					// fromUser = stdIn.readLine();
					// if (fromUser != null) {
					// System.out.println("Client: " + fromUser);
					// out.println(fromUser);
					// }
				}
				in.close();
				kkSocket.close();
			} catch (NumberFormatException e) {

			} catch (IOException e) {
			}

			// out.close();

		}
	};

}
