/*
    APRS-TAK Plugin
    Copyright (C) 2021-2022  niccellular@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.atakmap.android.aprstak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;

import com.atakmap.android.aprstak.plugin.PluginLifecycle;


public class AutoBeacon implements Runnable {
    private Thread thread;
    private boolean running;

    final static String TAG = "AutoBeacon";

    public AutoBeacon(){
        this.running = true;
        this.thread = new Thread(this);
    }
    @Override
    public void run() {
        Looper.prepare();

        SharedPreferences sharedPref = PluginLifecycle.activity.getSharedPreferences("aprs-prefs", Context.MODE_PRIVATE);
        int delay = sharedPref.getInt("autoBeaconInterval", 1);
        try {
            while(running) {
                Log.i(TAG, "APRS-TAK sending one POSITION packet");
                Intent i = new Intent("org.aprsdroid.app.ONCE").setPackage("org.aprsdroid.app");
                PluginLifecycle.activity.getApplicationContext().startForegroundService(i);
                Thread.sleep(delay*60000); // convert minutes
            }
        } catch(Exception e) {
            Log.i(TAG, "AutoBeacon thread error: " + e);
        }
    }
    public void start()  {
        thread.start();
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

}
