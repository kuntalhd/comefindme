package comefindme.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gtalkservice.IGTalkSession;
import comefindme.main.GTalkDataMessageService.GTalkSessionCreatedListener;

public class ComeFindMe extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		Button requestButton = (Button) findViewById(R.id.requestBtn);
		requestButton.setOnClickListener(this);

		bindService(new Intent(this, GTalkDataMessageService.class), new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				GTalkDataMessageService gTalkService = ((GTalkDataMessageService.LocalBinder) service).getService();
				gTalkService.addGTalkSessionCreatedListener(new GTalkSessionCreatedListener() {

					public void onGTalkSessionCreated(IGTalkSession session) {
						try {
							String header = "Logged in as " + session.getUsername();
							TextView textView = (TextView) findViewById(R.id.loggedInAsTextView);
							textView.setText(header);
						} catch (DeadObjectException e) {
							e.printStackTrace();
						}
					}

				});
			}

			public void onServiceDisconnected(ComponentName arg0) {

			}
		}, Context.BIND_AUTO_CREATE);
	}

	public void onClick(View view) {
		final String username = ((TextView) findViewById(R.id.usernameEditText)).getText().toString();

		registerReceiver(new LocationResponseHandler(username), new IntentFilter(ComeFindMeConstants.LOCATION_RESPONSE_ACTION));
		bindService(new Intent(this, GTalkDataMessageService.class), new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				final GTalkDataMessageService gTalkService = ((GTalkDataMessageService.LocalBinder) service).getService();

				gTalkService.addGTalkSessionCreatedListener(new GTalkSessionCreatedListener() {

					public void onGTalkSessionCreated(IGTalkSession session) {
						Intent intent = new Intent(ComeFindMeConstants.LOCATION_REQUEST_ACTION);
						gTalkService.sendDataMessage(username, intent);
					}

				});
			}

			public void onServiceDisconnected(ComponentName arg0) {

			}
		}, Context.BIND_AUTO_CREATE);
	}
}
