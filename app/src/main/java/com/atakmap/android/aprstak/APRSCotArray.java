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

import android.util.Log;

import com.atakmap.android.maps.MapEvent;
import com.atakmap.coremap.cot.event.CotEvent;

import java.util.HashMap;

public class APRSCotArray {

    private static final String TAG = "APRSCotArray";
    private static HashMap<String, CotEvent> cotEventsList;
    private static HashMap<String, MapEvent> mapEventsList;

    public APRSCotArray() {
        cotEventsList = new HashMap<>();
        mapEventsList = new HashMap<>();
    }

    public CotEvent getCotEvent(String uid) {
        if (hasCotEvent(uid))
            return cotEventsList.get(uid);
        return null;
    }

    public boolean hasCotEvent(String uid) {
        if (cotEventsList.containsKey(uid))
            return true;
        return false;
    }

    public void addCotEventToAPRSCotArray(CotEvent e) {
        String uid = e.getUID();
        Log.d(TAG, "Adding CotEvent uid: " + uid);
        cotEventsList.put(uid, e);
    }

    public void removeCotEventToAPRSCotArray(String uid) {
        Log.d(TAG, "Removing CotEvent uid: " + uid);
        cotEventsList.remove(uid);
    }

    public MapEvent getMapEvent(String uid) {
        if (hasMapEvent(uid))
            return mapEventsList.get(uid);
        return null;
    }

    public boolean hasMapEvent(String uid) {
        if (mapEventsList.containsKey(uid))
            return true;
        return false;
    }

    public void addMapEventToAPRSCotArray(MapEvent e) {
        String uid = e.getItem().getUID();
        Log.d(TAG, "Adding MapEvent uid: " + uid);
        mapEventsList.put(uid, e);
    }

    public void removeMapEventToAPRSCotArray(String uid) {
        Log.d(TAG, "Removing CotEvent uid: " + uid);
        mapEventsList.remove(uid);
    }
}
