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
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends Activity {
    Button bouton_capture_instantane;
    Button bouton_capture_5s;
    Button bouton_capture_10s;
    Button bouton_enregistrer_lieu;
    Button bouton_estimer_lieu;
    private EditText entree_lieu;
    EditText texte_lieu_estimation;
    private volatile boolean measuring;
    private long derniere = 0;
    private long maintenant;
    private long entre_mesures = 500; // en ms

    private ArrayList<List<ScanResult>> serie_mesures = new ArrayList<>();


    WifiManager wm;
    String FILENAME = "wifi_data";
    String JSON_EXTENSION = ".json";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bouton_capture_instantane = (Button) findViewById(R.id.bouton_capture_instantane);
        bouton_capture_5s = (Button) findViewById(R.id.bouton_capture_5s);
        bouton_capture_10s = (Button) findViewById(R.id.bouton_capture_10s);
        bouton_estimer_lieu = (Button) findViewById(R.id.bouton_estimer_lieu);

        entree_lieu = (EditText) findViewById(R.id.entree_lieu);
        texte_lieu_estimation = (EditText) findViewById(R.id.texte_lieu_estimation);


        verifyStoragePermissions( MainActivity.this);

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

        bouton_capture_instantane.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                scan_wifi_signals(5000);
            }
        });

        bouton_capture_10s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_wifi_signals(10000);
            }
        });


        bouton_estimer_lieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void scan_wifi_signals(final long duration)
    {


        if(duration == 0)
        {
            store_capture(wm.getScanResults());
        }
        else {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    long debut = System.currentTimeMillis();
                    setButtonsClickable(false);
                    serie_mesures = new ArrayList<>();
                    while (measuring) {
                        maintenant = System.currentTimeMillis();
                        if (maintenant - derniere > entre_mesures) {
                            serie_mesures.add(wm.getScanResults());
                            derniere = maintenant;
                        }
                        if(maintenant - debut > duration)
                        {
                            measuring = false;
                        }
                    }
                    store_capture_lists(serie_mesures);
                    setButtonsClickable(true);
                }
            };
        Thread t = new Thread(r);
        t.start();
        }
    }

    public JSONArray from_scan_result_to_json(List<ScanResult> lsr)
    {
        Date currentTime = Calendar.getInstance().getTime();
        JSONArray json_array;
        JSONObject object;
        int i = 0;
        json_array = new JSONArray();
        for (ScanResult sr : lsr) {
            Log.d("SSID : " + sr.SSID, TAG);
            Log.d("BSSID : " + sr.BSSID, TAG);
            Log.d("capabilities : " + sr.capabilities, TAG);
            Log.d("frequency : " + sr.frequency, TAG);
            Log.d("level : " + sr.level, TAG);
            try {
                object = new JSONObject();
                object.put("ssid", sr.SSID);
                object.put("bssid", sr.BSSID);
                object.put("capabilities", sr.capabilities);
                object.put("frequency", sr.frequency);
                object.put("level", sr.level);
                object.put("timestamp", currentTime.toString());
                String lieu = entree_lieu.getText().toString();
                if(lieu.equals(""))
                {
                    object.put("location", "NOWHERE");
                }
                else
                {
                    object.put("location", lieu);
                }
                json_array.put(i, object);
                i++;
            } catch (JSONException e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Problème avec les données JSON");
            }
        }
        Log.d(TAG, json_array.toString());
        return json_array;
    }

    public void store_capture(List<ScanResult> lsr)
    {

        Date currentTime = Calendar.getInstance().getTime();
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        JSONArray json_array;
        // converting
        json_array = from_scan_result_to_json(lsr);

        // storing
        try {
            File dirPublicDocuments =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!dirPublicDocuments.mkdirs()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "tous les sous-dossiers" + dirPublicDocuments.getAbsolutePath() +
                            " existent déjà");
                }
            }
            if (BuildConfig.DEBUG)
                Log.d(TAG, dirPublicDocuments.toString());

            File json_file = new File(dirPublicDocuments, FILENAME + currentTime.toString() + JSON_EXTENSION);
            fos = new FileOutputStream(json_file);
            osw = new OutputStreamWriter(fos);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, json_array.toString());
                Log.d(TAG, osw.toString());
            }

            osw.write(json_array.toString());
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "new FileOutputStream()", e);
        } catch (IOException e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "problème io");
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, "osw.close()", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, "fos.close()", e);
                }
            }
        }

    }

    public void store_capture_lists(List<List<ScanResult>> llsr)
    {
        Date currentTime = Calendar.getInstance().getTime();
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        JSONArray l_array;
        JSONArray json_array;
        int j = 0;
        l_array = new JSONArray();
        // converting
        for(List<ScanResult> lsr: llsr)
        {
            json_array = from_scan_result_to_json(lsr);

            try {
                l_array.put(j, json_array);
            } catch (JSONException e) {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Problème avec les données JSON");
            }
            j++;
        }

        // storing
        try {
            File dirPublicDocuments =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!dirPublicDocuments.mkdirs()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "tous les sous-dossiers" + dirPublicDocuments.getAbsolutePath() +
                            " existent déjà");
                }
            }
            if (BuildConfig.DEBUG)
                Log.d(TAG, dirPublicDocuments.toString());

            File json_file = new File(dirPublicDocuments, FILENAME + currentTime.toString() + JSON_EXTENSION);
            fos = new FileOutputStream(json_file);
            osw = new OutputStreamWriter(fos);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, l_array.toString());
            }
            osw.write(l_array.toString());
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "new FileOutputStream()", e);
        } catch (IOException e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "problème io");
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, "osw.close()", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, "fos.close()", e);
                }
            }
        }

    }

    public static void verifyStoragePermissions(Activity activity)
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

    public void setButtonsClickable(boolean focus)
    {

        bouton_capture_5s.setClickable(focus);
        bouton_capture_5s.setClickable(focus);
        bouton_capture_5s.setClickable(focus);
        bouton_capture_5s.setClickable(focus);
        bouton_capture_5s.setClickable(focus);
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
