package besnier.wifilocator.activities;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import besnier.wifilocator.BuildConfig;
import besnier.wifilocator.R;
import besnier.wifilocator.fingerprints.Fingerprint;
import besnier.wifilocator.fingerprints.FingerprintManager;

import static besnier.wifilocator.fingerprints.FingerprintManager.DEFAULT_FILENAME;

public class TrainingActivity extends AppCompatActivity {

    private static final String TAG = TrainingActivity.class.getSimpleName();

    private Button measureButton;
    private WifiManager wm;
    private FingerprintManager fpm;

    private EditText entree_lieu;

    private EditText entree_prefix;

    private RadioGroup durationMeasureRadioGroup;

    private volatile boolean measuring;
    private long derniere = 0;
    private long maintenant;
    private long entre_mesures = 500; // en ms
    private Context context;

    private ArrayList<List<ScanResult>> serie_mesures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        context = this;

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        fpm = new FingerprintManager(DEFAULT_FILENAME);

        durationMeasureRadioGroup = findViewById(R.id.durationRadioGroup);
        measureButton = findViewById(R.id.measureButton);
        entree_lieu = findViewById(R.id.entree_lieu);
        entree_prefix = findViewById(R.id.entree_prefix);

        durationMeasureRadioGroup.check(R.id.radioButtonInstantane);


        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "on a cliqué !");
                Log.d(TAG, "numéro : "+durationMeasureRadioGroup.getCheckedRadioButtonId());
                RadioButton rb = (RadioButton) findViewById(durationMeasureRadioGroup.getCheckedRadioButtonId());
                Log.d(TAG, "numéro : "+rb.getId());

                switch(rb.getId())
                {
                    case R.id.radioButtonInstantane:
                        store_capture(wm.getScanResults());
                        Toast.makeText(TrainingActivity.this, "Capture effective", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioButtonCinqSecondes:
                        Toast.makeText(TrainingActivity.this, "Capture effective pendant 5 s", Toast.LENGTH_SHORT).show();
                        scan_wifi_signals(5000);

                        break;
                    case R.id.radioButtonDixSecondes:
                        Toast.makeText(TrainingActivity.this, "Capture effective pendant 10 s", Toast.LENGTH_SHORT).show();
                        scan_wifi_signals(10000);
                        break;
                }

//                for (ScanResult sr : wm.getScanResults()) {
//                    Log.d("SSID : " + sr.SSID, TAG);
//                    Log.d("BSSID : " + sr.BSSID, TAG);
//                    Log.d("capabilities : " + sr.capabilities, TAG);
//                    Log.d("frequency : " + sr.frequency, TAG);
//                    Log.d("level : " + sr.level, TAG);
//                }
            }
        });
    }

    private void scan_wifi_signals(final long duration)
    {
        if(duration == 0)
        {
            store_capture(wm.getScanResults());
        }
        else {

            measuring = true;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    long measure_beginning = System.currentTimeMillis();
//                    setButtonsClickable(false);
                    serie_mesures = new ArrayList<>();
                    derniere = System.currentTimeMillis();
                    // On commence les mesures
                    while (measuring)
                    {
                        maintenant = System.currentTimeMillis();
                        if (maintenant - derniere > entre_mesures) {
                            serie_mesures.add(wm.getScanResults());
                            derniere = maintenant;
                        }
                        if(maintenant - measure_beginning > duration)
                        {
                            measuring = false;
                        }
                    }
                    Log.d(TAG, "taille de la liste de mesures : " + serie_mesures.size());
                    store_capture_lists(serie_mesures);
//                    setButtonsClickable(true);
                }
            };

            Thread t = new Thread(r);
            t.start();
        }
    }


    private void store_capture(List<ScanResult> lsr)
    {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        Fingerprint fp = new Fingerprint(lsr, entree_lieu.getText().toString(), timestamp);
        File f = new File(getFilesDir(), entree_prefix.getText().toString());
        fpm.storeFingerprints(fp, f);
    }

    private void store_capture_lists(List<List<ScanResult>> llsr)
    {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        ArrayList<Fingerprint> lfp = new ArrayList<>();
        Fingerprint fp;
        if (BuildConfig.DEBUG)
            Log.d(TAG, "store list capture");
        for(List<ScanResult> lsr: llsr)
        {
            fp = new Fingerprint(lsr, entree_lieu.getText().toString(), currentTime);
            lfp.add(fp);
        }
        File file = new File(getFilesDir(), entree_prefix.getText().toString());
        fpm.storeFingerprints(lfp, file);

    }
}
