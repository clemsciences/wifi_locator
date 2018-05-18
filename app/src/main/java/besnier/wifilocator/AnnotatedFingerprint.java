package besnier.wifilocator;

import android.net.wifi.ScanResult;

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
}
