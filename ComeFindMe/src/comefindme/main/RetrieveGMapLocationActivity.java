package comefindme.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Point;
import comefindme.main.SMSService.SMSListener;

public class RetrieveGMapLocationActivity extends MapActivity {

	private MapView mapView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.mapView = new MapView(this);
		setContentView(this.mapView);

		mapView.displayZoomDialog(0.0f, 0.0f);

		startSMSService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, "Send Location");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(Menu.Item item) {
		if (item.getId() == 1) {

			Point mapCenter = mapView.getMapCenter();

			Bundle data = new Bundle();
			data.putString(ComeFindMeConstants.LATITUDE_PROPERTY, String.valueOf(mapCenter.getLatitudeE6()));
			data.putString(ComeFindMeConstants.LONGITUDE_PROPERTY, String.valueOf(mapCenter.getLongitudeE6()));
			setResult(RESULT_OK, null, data);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void startSMSService() {
		bindService(new Intent(this, SMSService.class), new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				SMSService smsService = ((SMSService.LocalBinder) service).getService();
				smsService.addSMSListener(new SMSListener() {

					private int startX;
					private int startY;
					private boolean started = false;
					private int offset = 10;

					public void handleSMSValues(int x, int y, int z) {
						if (!started) {
							startX = x;
							startY = y;
							started = true;
						} else if ((x > (startX + offset)) || (y > (startY + offset)) || (x < (startX - offset)) || (y < (startY - offset))) {
							Point center = mapView.getMapCenter();
							centerMap((center.getLatitudeE6() + y), (center.getLongitudeE6() + (x * -1)));
						}
					}

					private void centerMap(int lat, int lon) {
						mapView.getController().centerMapTo(new Point(lat, lon), true);
					}
				});
			}

			public void onServiceDisconnected(ComponentName arg0) {

			}
		}, Context.BIND_AUTO_CREATE);
	}

}
