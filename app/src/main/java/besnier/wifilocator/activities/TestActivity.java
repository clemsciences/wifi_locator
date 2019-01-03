package besnier.wifilocator.activities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import besnier.wifilocator.R;
import besnier.wifilocator.fingerprints.Fingerprint;
import besnier.wifilocator.fingerprints.FingerprintManager;

import static besnier.wifilocator.fingerprints.FingerprintManager.DEFAULT_FILENAME;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
    private Button bouton_balise_plus_proche;

    private EditText entree_balise_plus_proche;



    private WifiManager wm;
    private FingerprintManager fpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        entree_balise_plus_proche = findViewById(R.id.entree_balise_plus_proche);
        bouton_balise_plus_proche = findViewById(R.id.bouton_balise_plus_proche);


        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        fpm = new FingerprintManager(DEFAULT_FILENAME);


        bouton_balise_plus_proche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "on a cliqué !");
                long timestamp = Calendar.getInstance().getTimeInMillis();
                Fingerprint fp = new Fingerprint(wm.getScanResults(), timestamp);
                String nearestSSID = fpm.findNearestBeacon(fp);
                Log.d(TAG, "info : " + fp.toJSON());
                entree_balise_plus_proche.setText(nearestSSID);
                Toast.makeText(TestActivity.this, "Mesure terminée", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
