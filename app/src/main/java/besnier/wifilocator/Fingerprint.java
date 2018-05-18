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
    private long timestamp = 0;
    private static final String TAG = Fingerprint.class.getSimpleName();
    public ArrayList<Long> vectorizedMeasure = new ArrayList<>();




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
    public Fingerprint()
    {
    }

    public void fromJSON(JSONObject json_object) throws JSONException {
        if(json_object.has("location"))
        {
            location = json_object.getString("location");
        }
        if(json_object.has("fingerprint"))
        {
            JSONArray fingerprint = json_object.getJSONArray("fingerprint");


        }



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
                json_array.put(i, bm.toJSON());
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

    public void vectorizeMeasure(VectorizedBeacons vb)
    {
        boolean found = false;
        for(String bssid : vb.vect_bssid)
        {
            for(BeaconMeasure bm : lbm)
            {
                if (bm.getBssid().equals(bssid))
                {
                    vectorizedMeasure.add(bm.getLevel());
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                vectorizedMeasure.add((long) 0);
            }

        }

    }

    public long distanceVectorizedMeasure(Fingerprint other_fp)
    {
        if(other_fp.vectorizedMeasure.size() == vectorizedMeasure.size())
        {
            int i;
            long distance = 0;
            for(i = 0; i < vectorizedMeasure.size();i++)
            {
                distance = Math.abs(vectorizedMeasure.get(i) - other_fp.vectorizedMeasure.get(i));
            }
            return distance;
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}
