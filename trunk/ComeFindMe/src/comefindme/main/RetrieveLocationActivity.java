package comefindme.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gtalkservice.IGTalkSession;
import comefindme.main.GTalkDataMessageService.GTalkSessionCreatedListener;

public class RetrieveLocationActivity extends Activity {

	private static final int GMAP_LOCATION_ACTIVITY = 1;
	private static final int LAT_LON_LOCATION_ACTIVITY = 2;

	@Override
	protected void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.retrieve_location);

		String from = getIntent().getStringExtra(ComeFindMeConstants.FROM_USERNAME_PROPERTY);

		new AlertDialog.Builder(this).setIcon(R.drawable.star_big_on).setTitle(from + " is requesting your location. Do you want to provide it?").setItems(R.array.locationRequestItems, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case 0:
					sendCoordinates("-37833844", "144944944");
					break;
				case 1:
					sendLatAndLonLocation();
					break;
				case 2:
					sendGMapLocation();
					break;
				case 3:
					sendGPSLocation();
					break;
				default:
					sendCoordinates("-37833844", "144944944");
					break;
				}

			}
		}).show();
	}

	private void sendLatAndLonLocation() {
		Intent i = new Intent(this, RetrieveLatAndLonLocationActivity.class);
		startSubActivity(i, LAT_LON_LOCATION_ACTIVITY);
	}

	private void sendGPSLocation() {

	}

	private void sendGMapLocation() {
		Intent i = new Intent(this, RetrieveGMapLocationActivity.class);
		startSubActivity(i, GMAP_LOCATION_ACTIVITY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
		super.onActivityResult(requestCode, resultCode, data, extras);
		sendCoordinates(extras.getString(ComeFindMeConstants.LATITUDE_PROPERTY), extras.getString(ComeFindMeConstants.LONGITUDE_PROPERTY));
	}

	private void sendCoordinates(final String lat, final String lon) {
		bindService(new Intent(RetrieveLocationActivity.this, GTalkDataMessageService.class), new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				final GTalkDataMessageService gTalkService = ((GTalkDataMessageService.LocalBinder) service).getService();

				gTalkService.addGTalkSessionCreatedListener(new GTalkSessionCreatedListener() {
					public void onGTalkSessionCreated(IGTalkSession session) {
						Intent intent = new Intent(ComeFindMeConstants.LOCATION_RESPONSE_ACTION);
						intent.putExtra(ComeFindMeConstants.LATITUDE_PROPERTY, lat);
						intent.putExtra(ComeFindMeConstants.LONGITUDE_PROPERTY, lon);
						gTalkService.sendDataMessage(getIntent().getStringExtra(ComeFindMeConstants.FROM_JID_PROPERTY), intent);
					}
				});

				RetrieveLocationActivity.this.finish();
			}

			public void onServiceDisconnected(ComponentName arg0) {

			}
		}, Context.BIND_AUTO_CREATE);
	}

}
