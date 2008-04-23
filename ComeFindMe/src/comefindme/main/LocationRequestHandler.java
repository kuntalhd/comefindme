package comefindme.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;

public class LocationRequestHandler extends IntentReceiver {

	@Override
	public void onReceiveIntent(final Context context, Intent intent) {
		Intent i = new Intent(context, RetrieveLocationActivity.class);
		i.addLaunchFlags(Intent.NEW_TASK_LAUNCH);
		i.putExtras(intent);
		context.startActivity(i);
	}
}
