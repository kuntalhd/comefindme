package comefindme.main;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Point;

public class RetrieveGMapLocationActivity extends MapActivity {

	private MapView mapView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.mapView = new MapView(this);
		setContentView(this.mapView);

		mapView.displayZoomDialog(0.0f, 0.0f);
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

}
