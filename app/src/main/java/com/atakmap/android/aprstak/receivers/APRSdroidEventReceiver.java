package com.atakmap.android.aprstak.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;


import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.contact.GroupContact;
import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.time.CoordinatedTime;


import utils.AprsUtility;

public class APRSdroidEventReceiver extends BroadcastReceiver {

	public static final String TAG = APRSdroidEventReceiver.class.getSimpleName();

	public APRSdroidEventReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent == null) {
			Log.w(TAG, "Doing nothing, because intent was null");
			return;
		}

		if (intent.getAction() == null) {
			Log.w(TAG, "Doing nothing, because intent action was null");
			return;
		}

		String a = intent.getAction().replace("org.aprsdroid.app.", "");

		switch (a) {
			case "SERVICE_STARTED":
				AprsUtility.aprsdroid_running = true;
				Log.i(TAG, "APRSdroid is running");
				break;
			case "SERVICE_STOPPED":
				AprsUtility.aprsdroid_running = false;
				Log.i(TAG, "APRSdroid is not running");
				break;
			case "MESSAGE":
			case "MESSAGETX":
				String source = intent.getStringExtra("source");
				String dest = intent.getStringExtra("dest");
				String body = intent.getStringExtra("body");
				Log.d("APRS Message Event", "message from: " + source + " to: " + dest + " body: " + body);
				String[] recipients = {dest};
				Bundle msg = ChatManagerMapComponent.getInstance().buildMessage("body", new GroupContact(source, source, false), recipients);
				ChatManagerMapComponent.getInstance().sendMessage(msg, recipients);
				break;
			case "POSITION":
				String callsign = intent.getStringExtra("callsign");
				String packet = intent.getStringExtra("packet");
				Location location = intent.getParcelableExtra("location");
				Log.d("APRS Position Event", "callsign: " + callsign + " position: " + location + "\nraw packet: " + packet);

				CotEvent event = new CotEvent();
				event.setUID(callsign);
				event.setType("a-f-G-U-C");
				event.setHow("m-g");

				CotPoint point = new CotPoint(location.getLatitude(), location.getLongitude(), 0,0,0);
				event.setPoint(point);

				long time =  new CoordinatedTime().getMilliseconds();
				event.setTime(new CoordinatedTime(time));
				event.setStart(new CoordinatedTime(time));
				event.setStale(new CoordinatedTime(time+300000000));

				CotDetail detail = new CotDetail("detail");
				detail.setInnerText(packet);
				event.setDetail(detail);

				CotMapComponent.getInternalDispatcher().dispatch(event);
				break;
			case "UPDATE":
				int type = intent.getIntExtra("type",2); // 2: error (something went wrong)
				String status = intent.getStringExtra("status");
				Log.d("APRS Update Event", type + " status: " + status);
				break;
		}

	}
}
