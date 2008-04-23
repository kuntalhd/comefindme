package comefindme.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;
import com.google.googlenav.DrivingDirection;
import com.google.googlenav.map.MapPoint;

public class ComeFindMeMapResponseActivity extends MapActivity {

	private DrivingDirection myDD = null;
	private boolean foundDirections = false;
	private int otherLat;
	private int otherLon;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.comefindme_map_response);

		this.otherLat = Integer.valueOf(this.getIntent().getStringExtra(ComeFindMeConstants.LATITUDE_PROPERTY));
		this.otherLon = Integer.valueOf(this.getIntent().getStringExtra(ComeFindMeConstants.LONGITUDE_PROPERTY));

		this.mapView = (MapView) findViewById(R.id.responseMapView);
		mapView.getController().centerMapTo(new Point(this.otherLat, this.otherLon), true);
		mapView.getController().zoomTo(21);

		OverlayController myOC = mapView.createOverlayController();
		/* Add a new instance of our fancy Overlay-Class to the MapView. */

		myOC.add(new MyMapDrivingDirectionsOverlay(this), true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, "Get Directions to My Location");
		menu.add(0, 2, "Another Location Request");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(Menu.Item item) {
		if (item.getId() == 1) {
			MapPoint mpFrom = new MapPoint(this.otherLat, this.otherLon);

			int myLat = this.mapView.getMapCenter().getLatitudeE6();
			int myLon = this.mapView.getMapCenter().getLongitudeE6();

			MapPoint mpTo = new MapPoint(myLat, myLon);
			this.startFetchDirections(mpFrom, "", mpTo, "");

		} else if (item.getId() == 2) {
			startActivity(new Intent(this, ComeFindMe.class));
		}
		return super.onOptionsItemSelected(item);
	}

	/** Offers the DrivingDirections to the Overlay. */
	public DrivingDirection getDrivingDirections() {
		return this.myDD;
	}

	private void startFetchDirections(MapPoint from_pos, String from_name, MapPoint to_pos, String to_name) {
		/*
		 * mDD is a class variable for the activity that will hold an instance
		 * of the DrivingDirection object created here.
		 */
		this.myDD = new DrivingDirection(from_pos, from_name, to_pos, to_name);
		if (this.myDD != null) {
			/* Add the request the dispatcher */
			this.getDispatcher().addDataRequest(this.myDD);

			Thread t = new Thread(new Runnable() {
				public void run() {
					/* Wait for the search to be complete... */
					while (!ComeFindMeMapResponseActivity.this.myDD.isComplete()) {
						try {
							Thread.sleep(100);
							Log.e("DEBUGTAG", "'!MyDrivingDirectionsActivity.this.myDD.isComplete()' was interruoted.");
						} catch (InterruptedException e) {
							Log.e("DEBUGTAG", "'!MyDrivingDirectionsActivity.this.myDD.isComplete()' was interruoted.", e);
						}
					}
					/*
					 * Check to see if any Placemarks were found.. if 0 then
					 * there is no route!
					 */
					Log.e("DEBUGTAG", "'!MyDrivingDirectionsActivity.this.myDD.isComplete()' was interruoted.");
					if (ComeFindMeMapResponseActivity.this.myDD.getState() != DrivingDirection.SUCCESS_STATUS) {
						/*
						 * Set a flag to let the program know the directions are
						 * done...
						 */
						ComeFindMeMapResponseActivity.this.foundDirections = true;

					} else { /* no route.. */
						ComeFindMeMapResponseActivity.this.foundDirections = true;
						ComeFindMeMapResponseActivity.this.myDD = null;
						/* Let the user know that no route was found... */
					}
				}
			});
			t.start();
		}
	}
}
