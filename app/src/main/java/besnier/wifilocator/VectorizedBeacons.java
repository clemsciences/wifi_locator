package besnier.wifilocator;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by clement_besnier on 18/05/2018.
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

    public JSONArray toJSON()
    {
        JSONArray json_array = new JSONArray();
        for(String bssid : vect_bssid)
        {
            json_array.put(bssid);
        }
        return json_array;
    }
}
