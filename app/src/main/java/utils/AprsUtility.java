package utils;

import android.content.Context;
import android.content.Intent;

import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapEvent;
import com.atakmap.android.maps.MapEventDispatcher;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.PointMapItem;
import com.atakmap.android.menu.PluginMenuParser;
import com.atakmap.coremap.log.Log;

import java.util.Collection;
import java.util.HashSet;

import com.atakmap.android.aprstak.AutoBeacon;


public class AprsUtility extends DropDownReceiver implements DropDown.OnStateListener, MapEventDispatcher.MapEventDispatchListener  {
    public static final String TAG = AprsUtility.class
            .getSimpleName();

    private static AprsUtility instance = null;
    private MapView mapView;
    private Context context;
    private boolean isReceiving = false;

    public static AutoBeacon ab = null;
    private boolean isAutoBeaconing = false;

    // TNC
    public static boolean useTNC = false;
    public static boolean aprsdroid_running = false;

    /**
     * CotUtility utility for sending and receiving cursor on target messages via ax.25
     *
     * @param mapView
     * @param context
     * @return CotUtility instance
     */
    public static AprsUtility getInstance(MapView mapView, Context context){
        if(instance == null){
            instance = new AprsUtility(mapView, context);
        }
        return instance;
    }

    public static Collection<MapItem> getMapItemsInGroup(MapGroup mapGroup, Collection<MapItem> mapItems){
        Collection<MapGroup> childGroups = mapGroup.getChildGroups();
        if(childGroups.size() == 0){
            return mapGroup.getItems();
        }else {
            for (MapGroup childGroup : childGroups) {
                mapItems.addAll(getMapItemsInGroup(childGroup, mapItems));
            }
        }

        return mapItems;
    }

    private AprsUtility(MapView mapView, Context context) {
        super(mapView);
        this.mapView = mapView;
        this.context = context;


        Collection<MapItem> mapItems = getMapItemsInGroup(getMapView().getRootGroup(), new HashSet<MapItem>());
        for(MapItem mapItem : mapItems){
            mapItem.setMetaString("menu", getMenu());
        }

        getMapView().getMapEventDispatcher().addMapEventListener(MapEvent.ITEM_ADDED, this);
    }

    @Override
    public void onDropDownSelectionRemoved() {

    }

    @Override
    public void onDropDownClose() {

    }

    @Override
    public void onDropDownSizeChanged(double v, double v1) {

    }

    @Override
    public void onDropDownVisible(boolean b) {

    }

    @Override
    protected void disposeImpl() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
    }

    public void startABListener() {
        Log.d(TAG, "startAutoBeacon"); 
        isAutoBeaconing = true;

        ab = new AutoBeacon();
        ab.start();
    }

    public void stopABListener() {
        Log.d(TAG, "stopAutoBeacon");
        isAutoBeaconing = false;

        if(ab == null) {
            return;
        }

        ab.stop();
    }

    public boolean isAutoBeaconing(){
        return isAutoBeaconing;
    }


    private String getMenu() {
        return PluginMenuParser.getMenu(context, "menu.xml");

    }

    @Override
    public void onMapEvent(MapEvent mapEvent) {
        mapEvent.getItem().setMetaString("menu", getMenu());
    }

}
