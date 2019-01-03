package besnier.wifilocator.activities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import besnier.wifilocator.R;
import besnier.wifilocator.beacons.VectorizedBeacons;
import besnier.wifilocator.fingerprints.AnnotatedFingerprint;
import besnier.wifilocator.fingerprints.Fingerprint;
import besnier.wifilocator.fingerprints.FingerprintManager;
import besnier.wifilocator.format.Position;

import static besnier.wifilocator.fingerprints.FingerprintManager.DEFAULT_FILENAME;

public class EstimationActivity extends AppCompatActivity {
    private static final String TAG = EstimationActivity.class.getSimpleName();

    private EditText entree_lieu_estimation;
    private EditText entree_position_estimation;

    private Button bouton_estimer_lieu;
    private Button bouton_estimer_position;

    private EditText entree_prefix;

    private WifiManager wm;
    private FingerprintManager fpm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimation);

        bouton_estimer_lieu = findViewById(R.id.bouton_estimer_lieu);
        bouton_estimer_position = findViewById(R.id.bouton_estimer_position);
        entree_lieu_estimation = findViewById(R.id.entree_lieu_estimation);
        entree_position_estimation = findViewById(R.id.entree_position_estimation);
        entree_prefix = findViewById(R.id.entree_prefix);

        fpm = new FingerprintManager(DEFAULT_FILENAME);

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        bouton_estimer_lieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timestamp = Calendar.getInstance().getTimeInMillis();
                Fingerprint fp_now = new Fingerprint(wm.getScanResults(), "", timestamp);
                String prefix = entree_prefix.getText().toString();
                ArrayList<Fingerprint> lfp = FingerprintManager.loadFingerprints(new File(getFilesDir(), prefix));
                VectorizedBeacons vb = new VectorizedBeacons(lfp);
                fp_now.estimateLocation(lfp, vb);
                entree_lieu_estimation.setText(fp_now.getLocation());
            }
        });

        bouton_estimer_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timestamp = Calendar.getInstance().getTimeInMillis();
                Fingerprint fp = new Fingerprint(wm.getScanResults(), "", timestamp);

                String prefix = entree_prefix.getText().toString();


                ArrayList<AnnotatedFingerprint> lafp = FingerprintManager.loadAnnotatedFingerprints(new File(getFilesDir(), prefix));
                VectorizedBeacons vb = new VectorizedBeacons(lafp);
                Position pos = fpm.estimatePosition(lafp, fp, vb);
                entree_position_estimation.setText(pos.toString());
            }
        });

    }
}
