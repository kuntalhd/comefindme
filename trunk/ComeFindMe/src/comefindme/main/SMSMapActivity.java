package comefindme.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Point;
import comefindme.main.SMSService.SMSListener;

public class SMSMapActivity extends MapActivity {

	private MapView mapView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.mapView = new MapView(this);
		this.mapView.getController().zoomTo(21);
		setContentView(this.mapView);
		startSMSService();
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
