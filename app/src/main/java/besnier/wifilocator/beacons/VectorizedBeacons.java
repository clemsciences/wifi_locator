package besnier.wifilocator.beacons;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import besnier.wifilocator.fingerprints.AnnotatedFingerprint;
import besnier.wifilocator.fingerprints.Fingerprint;

/**
 * Created by clement_besnier on 18/05/2018.
 *
 * JSON conversion is used to store and retrieve data
 */

public class VectorizedBeacons {

    public ArrayList<String> vect_bssid = new ArrayList<String>();


    public VectorizedBeacons(List<Fingerprint> lfp)
    {
        Set<String> set_bssid = new HashSet<>();
        for(Fingerprint fp : lfp)
        {
            for(BeaconMeasure bm : fp.lbm)
            {
                set_bssid.add(bm.getBssid());
            }
        }
        vect_bssid.addAll(set_bssid);
    }

    public VectorizedBeacons(ArrayList<AnnotatedFingerprint> lafp) {
        Set<String> set_bssid = new HashSet<>();
        for(AnnotatedFingerprint afp : lafp)
        {
            for(BeaconMeasure bm : afp.lbm)
            {
                set_bssid.add(bm.getBssid());
            }
        }
        vect_bssid.addAll(set_bssid);
    }

    public JSONArray toJSON()
    {
        JSONArray json_array = new JSONArray();
        for(String bssid : vect_bssid)
        {
            json_array.put(bssid);
        }
        return json_array;
    }

    public String toString()
    {
        String res = "";
        for(String a : vect_bssid)
        {
            res = res + ", " + a;
        }
        return res;
    }
}
