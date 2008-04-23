package comefindme.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.widget.Toast;

public class LocationResponseHandler extends IntentReceiver {

	private String gTalkUsername;

	public LocationResponseHandler(String gTalkUsername) {
		this.gTalkUsername = gTalkUsername;
	}

	public void onReceiveIntent(Context context, Intent intent) {
		Intent i = new Intent(context, ComeFindMeMapResponseActivity.class);
		i.addLaunchFlags(Intent.NEW_TASK_LAUNCH);
		i.putExtras(intent);
		context.startActivity(i);
		Toast.makeText(context, "Received Location Response from " + intent.getStringExtra(ComeFindMeConstants.FROM_USERNAME_PROPERTY) + " - " + intent.getStringExtra(ComeFindMeConstants.LATITUDE_PROPERTY) + " - " + intent.getStringExtra(ComeFindMeConstants.LONGITUDE_PROPERTY), Toast.LENGTH_LONG).show();
	}

}