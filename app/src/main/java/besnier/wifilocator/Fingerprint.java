package besnier.wifilocator;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clement_besnier on 15/05/2018.
 */

public class Fingerprint {
    public List<BeaconMeasure> lbm = new ArrayList<>();
    private String location = "";
    JSONObject object = new JSONObject();
    private long timestamp;
    private static final String TAG = Fingerprint.class.getSimpleName();




    public Fingerprint(List<ScanResult> lsr, String location, long timestamp)
    {
        this.location = location;
        BeaconMeasure bm;

        for(ScanResult sr : lsr)
        {
            bm = new BeaconMeasure(sr.SSID, sr.BSSID, sr.capabilities, sr.frequency, sr.level, sr.timestamp);
            lbm.add(bm);
        }
        this.timestamp = timestamp;



    }

    public JSONObject toJSON()
    {
        int i =0;
        JSONArray json_array = new JSONArray();
        JSONObject element = new JSONObject();
        for(BeaconMeasure bm : lbm) // wm.getScanResults()
        {
            try {
                bm.toJSON();
                json_array.put(i, bm);
                i++;
            } catch (JSONException e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Problème avec les données JSON");
            }
        }

        try {
            element.put("fingerprint", json_array);


        if(location.equals(""))
        {
            element.put("location", "NOWHERE");
        }
        else
        {
            element.put("location", location);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, json_array.toString());
        return element;

    }
}


//
//                json_array.put(i, object);
//                        i++;



//            Log.d(TAG, "SSID : " + bm.getSsid());
//                    Log.d(TAG, "BSSID : " + bm.getBssid());
//                    Log.d(TAG, "capabilities : " + bm.getCapabilities());
//                    Log.d(TAG, "frequency : " + bm.getFrequency());
//                    Log.d(TAG, "level : " + bm.getLevel());