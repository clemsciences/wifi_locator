package besnier.wifilocator;

import android.net.wifi.ScanResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by clement_besnier on 18/05/2018.
 */

public class AnnotatedFingerprint extends Fingerprint {
    public Position pos;
    public AnnotatedFingerprint(List<ScanResult> lsr, String location, long timestamp, Position pos)
    {
        super(lsr, location, timestamp);
        this.pos = pos;

    }

    public AnnotatedFingerprint()
    {
        super();
        pos = new Position();
    }

    public void fromJSON(JSONObject json_object) throws JSONException {
        super.fromJSON(json_object);
        if(json_object.has("position"))
        {
            Position new_pos = new Position();
            new_pos.fromJSON(json_object.getJSONObject("position"));
            pos = new_pos;
        }
    }

}
