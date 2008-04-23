package comefindme.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RetrieveLatAndLonLocationActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.retrieve_lat_lon_location);

		Button sendBtn = (Button) findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(this);
	}

	public void onClick(View view) {
		if (view.getId() == R.id.sendBtn) {

			String lat = ((EditText) findViewById(R.id.latitudeField)).getText().toString();
			String lon = ((EditText) findViewById(R.id.longitudeField)).getText().toString();

			Bundle data = new Bundle();
			data.putString(ComeFindMeConstants.LATITUDE_PROPERTY, lat);
			data.putString(ComeFindMeConstants.LONGITUDE_PROPERTY, lon);
			setResult(RESULT_OK, null, data);
			finish();
		}
	}
}
