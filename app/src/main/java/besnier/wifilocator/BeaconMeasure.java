package besnier.wifilocator;

import android.net.wifi.ScanResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement_besnier on 15/05/2018.
 */

public class BeaconMeasure {
    private String ssid;
    private String bssid;
    private String capabilities;
    private int frequency;
    private long level;
    private long timestamp;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BeaconMeasure(String ssid, String bssid, String capabilities, int frequency, long level,
                         long timestamp)
    {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.frequency = frequency;
        this.level = level;

        this.timestamp = timestamp;

    }
    public BeaconMeasure(JSONObject jo)
    {
        if(jo.has("ssid"))
        {
            try {
                ssid = jo.getString("ssid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jo.has("bssid"))
        {
            try {
                bssid = jo.getString("bssid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jo.has("capabilities"))
        {
            try {
                capabilities = jo.getString("capabilities");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jo.has("frequency"))
        {
            try {
                frequency = jo.getInt("frequency");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jo.has("level"))
        {
            try {
                level = jo.getLong("level");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jo.has("timestamp"))
        {
            try {
                timestamp = jo.getLong("timestamp");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void fromJSON(JSONObject object) throws JSONException {

        if(object.has("ssid"))
            ssid = object.getString("ssid");
        if (object.has("bssid"))
            bssid = object.getString("bssid");
        if (object.has("capabilities"))
            capabilities = object.getString("capabilities");
        if (object.has("frequency"))
            frequency = object.getInt("frequency");
        if (object.has("level"))
            level = object.getLong("level");
        if (object.has("timestamp"))
            timestamp = object.getLong("timestamp");
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject object = new JSONObject();
        object.put("ssid", ssid);
        object.put("bssid", bssid);
        object.put("capabilities", capabilities);
        object.put("frequency", frequency);
        object.put("level", level);
        object.put("timestamp", timestamp);
        return object;
    }

    public JSONObject toJSON(ScanResult sr, long timestamp) throws JSONException {
        ssid = sr.SSID;
        bssid = sr.SSID;
        capabilities = sr.SSID;
        frequency = sr.frequency;
        level = sr.level;
        this.timestamp = timestamp;
        return toJSON();
    }


}
