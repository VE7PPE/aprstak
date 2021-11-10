package com.atakmap.android.aprstak;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import utils.AprsUtility;

import com.atakmap.android.aprstak.receivers.APRSdroidEventReceiver;

import com.atakmap.android.aprstak.receivers.ReadLogReceiver;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapEvent;
import com.atakmap.android.maps.MapEventDispatcher;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.android.aprstak.receivers.AprsDropDownReceiver;
import com.atakmap.android.aprstak.plugin.R;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.comms.CotServiceRemote;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.log.Log;



public class AprsMapComponent extends DropDownMapComponent implements CotServiceRemote.CotEventListener, MapEventDispatcher.MapEventDispatchListener {

    public static final String TAG = "PluginMain";

    public Context pluginContext;

    private AprsDropDownReceiver ddr;
    private MapView mapView;

    public void onCreate(final Context context, Intent intent,
            final MapView view) {

        this.mapView = view;
        view.getMapEventDispatcher().addMapEventListener(MapEvent.ITEM_ADDED,this);

        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);
        pluginContext = context;

        ddr = new AprsDropDownReceiver(
                view, context);

        Log.d(TAG, "registering the plugin filter");
        DocumentedIntentFilter ddFilter = new DocumentedIntentFilter();
        ddFilter.addAction(AprsDropDownReceiver.SHOW_PLUGIN);
        registerDropDownReceiver(ddr, ddFilter);

        CommsMapComponent.getInstance().addOnCotEventListener(this);

        Log.d(TAG, "Registering aprs receiver with intent filter");
        APRSdroidEventReceiver aprsDroidReceiver = new APRSdroidEventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("org.aprsdroid.app.SERVICE_STARTED");
        intentFilter.addAction("org.aprsdroid.app.SERVICE_STOPPED");
        intentFilter.addAction("org.aprsdroid.app.MESSAGE");
        intentFilter.addAction("org.aprsdroid.app.MESSAGETX");
        intentFilter.addAction("org.aprsdroid.app.POSITION");
        intentFilter.addAction("org.aprsdroid.app.UPDATE");
        pluginContext.registerReceiver(aprsDroidReceiver, intentFilter);

        ReadLogReceiver readMeReceiver = new ReadLogReceiver(view, context);
        registerReceiverUsingPluginContext(pluginContext, "readme receiver", readMeReceiver, ReadLogReceiver.SHOW_LOG);
    }


    private void registerReceiverUsingPluginContext(Context pluginContext, String name, DropDownReceiver rec, String actionName) {
        android.util.Log.d(TAG, "Registering " + name + " receiver with intent filter");
        AtakBroadcast.DocumentedIntentFilter mainIntentFilter = new AtakBroadcast.DocumentedIntentFilter();
        mainIntentFilter.addAction(actionName);
        this.registerReceiver(pluginContext, rec, mainIntentFilter);
    }

    private void registerReceiverUsingAtakContext(String name, DropDownReceiver rec, String actionName) {
        android.util.Log.d(TAG, "Registering " + name + " receiver with intent filter");
        AtakBroadcast.DocumentedIntentFilter mainIntentFilter = new AtakBroadcast.DocumentedIntentFilter();
        mainIntentFilter.addAction(actionName);
        AtakBroadcast.getInstance().registerReceiver(rec, mainIntentFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

    @Override
    public void onCotEvent(CotEvent cotEvent, Bundle bundle) {
        android.util.Log.d(TAG, "onReceiveMapEvent: " + cotEvent.toString());
    }

    @Override
    public void onMapEvent(MapEvent mapEvent) {
        android.util.Log.d(TAG, "onReceiveMapEvent: " + mapEvent.getType());
    }
}
