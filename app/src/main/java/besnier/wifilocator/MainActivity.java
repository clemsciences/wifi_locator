package besnier.wifilocator;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {
    private Button bouton_capture_instantane;
    private Button bouton_capture_5s;
    private Button bouton_capture_10s;
    private Button bouton_balise_plus_proche;
    private Button bouton_estimer_lieu;
    private Button bouton_estimer_position;
    private Button bouton_exporter;

    private EditText entree_lieu;
    private EditText entree_balise_plus_proche;
    private EditText entree_lieu_estimation;
    private EditText entree_position_estimation;
    private EditText entree_prefix;


    private volatile boolean measuring;
    private long derniere = 0;
    private long maintenant;
    private long entre_mesures = 500; // en ms

    private ArrayList<List<ScanResult>> serie_mesures = new ArrayList<>();


    private WifiManager wm;
    private FingerprintManager fpm;
    private String FILENAME = "wifi_data";

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_LOCATION = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final DBManager dbm = new DBManager(MainActivity.this);
        fpm = new FingerprintManager(FILENAME);

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bouton_capture_instantane = (Button) findViewById(R.id.bouton_capture_instantane);
        bouton_capture_5s = (Button) findViewById(R.id.bouton_capture_5s);
        bouton_capture_10s = (Button) findViewById(R.id.bouton_capture_10s);
        bouton_balise_plus_proche = (Button) findViewById(R.id.bouton_balise_plus_proche);
        bouton_estimer_lieu = (Button) findViewById(R.id.bouton_estimer_lieu);
        bouton_estimer_position = (Button) findViewById(R.id.bouton_estimer_position);
        bouton_exporter = (Button) findViewById(R.id.bouton_exporter);

        entree_lieu = (EditText) findViewById(R.id.entree_lieu);
        entree_lieu_estimation = (EditText) findViewById(R.id.entree_lieu_estimation);
        entree_balise_plus_proche = (EditText) findViewById(R.id.entree_balise_plus_proche);
        entree_position_estimation = (EditText) findViewById(R.id.entree_position_estimation);
        entree_prefix = (EditText) findViewById(R.id.entree_prefix);


        verifyStoragePermissions( MainActivity.this);
        verifyLocationPermissions(MainActivity.this);

        // Activate WIFI

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wifi");
        builder.setMessage("Voulez-vous activer le Wifi ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Non", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Oui", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        if (!wm.isWifiEnabled()) {
            builder.show();
        }
        // Soit on le stocke sur le portable, soit on l'envoie à quelqu'un avec l'adresse email.

//        File dirBase = Environment.getExternalStorageDirectory();
//        File dirAppBase = new File(dirBase.getAbsolutePath()+File.separator+
//        "Android"+File.separator+"data"+File.separator+getClass().getPackage().getName()+
//        File.separator+"files");
//        if(!dirAppBase.mkdirs()){
//            Log.d(TAG, "on crée tous les sous-dossiers");
//        }
//        Log.d(TAG, dirAppBase.toString());
        //enregistrement du fichier !

        bouton_capture_instantane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "on a cliqué !");
                store_capture(wm.getScanResults());
                for (ScanResult sr : wm.getScanResults()) {
                    Log.d("SSID : " + sr.SSID, TAG);
                    Log.d("BSSID : " + sr.BSSID, TAG);
                    Log.d("capabilities : " + sr.capabilities, TAG);
                    Log.d("frequency : " + sr.frequency, TAG);
                    Log.d("level : " + sr.level, TAG);
                }
            }
        });

        bouton_capture_5s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "on a cliqué !");
                scan_wifi_signals(5000);
            }
        });

        bouton_capture_10s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,
                        "on a cliqué !");
                scan_wifi_signals(10000);
            }
        });

        bouton_balise_plus_proche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "on a cliqué !");
                long timestamp = Calendar.getInstance().getTimeInMillis();
                Fingerprint fp = new Fingerprint(wm.getScanResults(), entree_lieu.getText().toString(), timestamp);
                String nearestSSID = fpm.findNearestBeacon(fp);
                Log.d(TAG, "info : "+fp.toJSON());
                entree_balise_plus_proche.setText(nearestSSID);
            }
        });


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
                Fingerprint fp = new Fingerprint(wm.getScanResults(), entree_lieu.getText().toString(), timestamp);

                String prefix = entree_prefix.getText().toString();


                ArrayList<AnnotatedFingerprint> lafp = FingerprintManager.loadAnnotatedFingerprints(new File(getFilesDir(), prefix));
                VectorizedBeacons vb = new VectorizedBeacons(lafp);
                Position pos = fpm.estimatePosition(lafp, fp, vb);
                entree_position_estimation.setText(pos.toString());
            }
        });

        final CharSequence exportation_success_text = "Données exportées dans le dossier Documents";
        final CharSequence exportation_failure_no_prefix_text = "Il faut préciser un préfixe valide";
        final CharSequence exportation_failure_unvalid_prefix_text = "Le préfixe donné n'est pas valide";

        bouton_exporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();

                int duration = Toast.LENGTH_SHORT;

                if(!entree_prefix.getText().toString().equals(""))
                {
                    File dirPublicDocuments =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

                    File folder_to_copy = new File(getFilesDir(), entree_prefix.getText().toString());
                    File new_folder = new File(dirPublicDocuments.toString(),entree_prefix.getText().toString());

                    boolean success = FingerprintManager.exportFingerprints(folder_to_copy, new_folder);
                    if(success) {
                        Toast.makeText(context, exportation_success_text, duration).show();
                    }
                    else
                    {
                        Toast.makeText(context, "Problème dans l'exportation pour des raisons inconnues", duration).show();
                    }

                }
                else
                {
                    Toast.makeText(context, exportation_failure_no_prefix_text, duration).show();
                }
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
                    setButtonsClickable(false);
                    serie_mesures = new ArrayList<>();
                    derniere = System.currentTimeMillis();
                    // On commence les mesures
                    while (measuring) {
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
                    setButtonsClickable(true);
                }
            };

        Thread t = new Thread(r);
        t.start();
        }
    }

    public JSONObject from_scan_result_to_json(List<ScanResult> lsr, String location) {

        long timestamp = Calendar.getInstance().getTimeInMillis();
        Fingerprint fp = new Fingerprint(lsr, location, timestamp);
        return fp.toJSON();

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

    private static void verifyLocationPermissions(Activity activity)
    {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if( permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    private static void verifyStoragePermissions(Activity activity)
    {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void setButtonsClickable(boolean focus)
    {

        bouton_capture_instantane.setClickable(focus);
//        bouton_capture_instantane.setEnabled(focus);
        bouton_capture_5s.setClickable(focus);
//        bouton_capture_5s.setEnabled(focus);
        bouton_capture_10s.setClickable(focus);
//        bouton_capture_10s.setEnabled(focus);
        bouton_estimer_lieu.setClickable(focus);
//        bouton_estimer_lieu.setEnabled(focus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
