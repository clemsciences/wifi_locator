package besnier.wifilocator.fingerprints;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import besnier.wifilocator.BuildConfig;
import besnier.wifilocator.beacons.BeaconMeasure;
import besnier.wifilocator.beacons.VectorizedBeacons;

/**
 * Created by clement_besnier on 15/05/2018.
 *
 */

public class Fingerprint {
    public List<BeaconMeasure> lbm = new ArrayList<>();

    private String location = "";
    private long timestamp = 0;
    private static final String TAG = Fingerprint.class.getSimpleName();
    public ArrayList<Long> vectorizedMeasure = new ArrayList<>();


    /**
     * location is not necessary when we estimate the location because we only refer to the locations of a given list of fingerprints
     * @param lsr list of scan results
     * @param timestamp when the measure was made
     */
    public Fingerprint(List<ScanResult> lsr, long timestamp)
    {
        for(ScanResult sr : lsr)
        {
            lbm.add(new BeaconMeasure(sr.SSID, sr.BSSID, sr.capabilities, sr.frequency, sr.level, sr.timestamp));
        }
        this.timestamp = timestamp;
    }


    /**
     * Used when the location is necessary : when the fingerprint is compared to a reference fingerprint
     * @param lsr list of scan measures
     * @param location given location
     * @param timestamp when it was measured
     */
    public Fingerprint(List<ScanResult> lsr, String location, long timestamp)
    {
        this.location = location;
        for(ScanResult sr : lsr)
        {
            lbm.add(new BeaconMeasure(sr.SSID, sr.BSSID, sr.capabilities, sr.frequency, sr.level, sr.timestamp));
        }
        this.timestamp = timestamp;
    }
    public Fingerprint()
    {
    }

    public void estimateLocation(List<Fingerprint> lfp, VectorizedBeacons vb)
    {
        long distance;
        long mini_distance = Long.MAX_VALUE;
        int i_mini_distance = 0;
        int i = 0;
        vectorizeMeasure(vb);
        for(Fingerprint fp : lfp)
        {
            fp.vectorizeMeasure(vb);
            distance = fp.distanceVectorizedMeasure(this);
            if(distance < mini_distance)
            {
                mini_distance = distance;
                i_mini_distance = i;
            }
            i ++;
        }
        location = lfp.get(i_mini_distance).getLocation();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void fromJSON(JSONObject json_object) throws JSONException {
        if(json_object.has("location"))
        {
            location = json_object.getString("location");
        }
        if(json_object.has("fingerprint"))
        {
            ArrayList<BeaconMeasure> transi_lbm = new ArrayList<>();
            JSONArray fingerprint_json = json_object.getJSONArray("fingerprint");
            for(int i = 0 ; i < fingerprint_json.length(); i ++)
            {
                BeaconMeasure bm = new BeaconMeasure((JSONObject) fingerprint_json.get(i));
                transi_lbm.add(bm);
            }
            lbm = transi_lbm;
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
            if(timestamp != 0)
            {
                element.put("timestamp", timestamp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, json_array.toString());
        return element;

    }

    static public String loadRawFile(File file)
    {
        if(file.isFile()) {
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                String resultat;
                while ((resultat = br.readLine()) != null) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(resultat);
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
            }
            return sb.toString();
        }
        else
        {
            return "";
        }
    }


    public void load(File file)
    {
        if(file.isFile())
        {
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                String resultat;
                while ((resultat = br.readLine()) != null) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(resultat);
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
            }

            try
            {
                JSONObject object = new JSONObject(sb.toString());
                if(BuildConfig.DEBUG)
                {
                    Log.d(TAG, object.toString());
                }
                fromJSON(object);
            }
            catch (JSONException e)
            {
                if(BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * @param vb vectore of beacons (it is like a list of beacon features
     */
    public void vectorizeMeasure(VectorizedBeacons vb)
    {
        boolean found;
        for(String bssid : vb.vect_bssid)
        {
            found = false;
            for(BeaconMeasure bm : lbm)
            {
                if (bm.getBssid().equals(bssid))
                {
                    vectorizedMeasure.add(bm.getLevel());
                    found = true;
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
                distance += Math.abs(vectorizedMeasure.get(i) - other_fp.vectorizedMeasure.get(i));
            }
            return distance;
        }
        else
        {
            Log.d(TAG, "taille 1 : " + vectorizedMeasure.size()+ " , taille 2 : "+other_fp.vectorizedMeasure.size());
            throw new UnsupportedOperationException();
        }
    }

    public String toStringvectorizedMeasure() {
        String res = "";
        for (long a : vectorizedMeasure) {
            res = res + ", " + a;
        }
        return res;
    }
}
