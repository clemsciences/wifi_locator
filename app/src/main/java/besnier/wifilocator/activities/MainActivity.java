package besnier.wifilocator.activities;

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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import besnier.wifilocator.R;
import besnier.wifilocator.fingerprints.Fingerprint;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private WifiManager wm;

    private Button measureActivityButton;
    private Button importExportActivityButton;
    private Button testButtonActivity;
    private Button estimationActivityButton;
    Button fingerprintFileManagerButton;


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

        measureActivityButton = findViewById(R.id.measureActivityButton);
        importExportActivityButton = findViewById(R.id.importExportActivityButton);
        testButtonActivity = findViewById(R.id.testButtonActivity);
        estimationActivityButton = findViewById(R.id.estimationActivityButton);
        fingerprintFileManagerButton = findViewById(R.id.fingerprintManagerActivityButton);

//        final DBManager dbm = new DBManager(MainActivity.this);

        verifyStoragePermissions(MainActivity.this);
        verifyLocationPermissions(MainActivity.this);

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Activate WIFI

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wifi");
        builder.setMessage("Voulez-vous activer le Wifi ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
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

        measureActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TrainingActivity.class);
                startActivity(i);
            }
        });

        importExportActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ImportExportActivity.class);
                startActivity(i);
            }
        });

        testButtonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TestActivity.class);
                startActivity(i);
            }
        });

        estimationActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EstimationActivity.class);
                startActivity(i);
            }
        });

        fingerprintFileManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FingerprintFileManagerActivity.class);
                startActivity(i);
            }
        });
    }


    public JSONObject from_scan_result_to_json(List<ScanResult> lsr, String location) {

        long timestamp = Calendar.getInstance().getTimeInMillis();
        Fingerprint fp = new Fingerprint(lsr, location, timestamp);
        return fp.toJSON();

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
