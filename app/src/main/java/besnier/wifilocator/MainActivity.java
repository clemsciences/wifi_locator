package besnier.wifilocator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button bou;
    WifiManager wm;
    String FILENAME = "wifi_data";
    String JSON_EXTENSION = ".json";
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bou = (Button) findViewById(R.id.bouton_capture_wifi);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Wifi");
        builder.setMessage("Voulez-vous activer le Wifi ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Non", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Oui", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        if (!wm.isWifiEnabled())
        {
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

        bou.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                store_capture(wm.getScanResults());
//                for (ScanResult sr : wm.getScanResults()) {
//                    Log.d("SSID : " + sr.SSID, TAG);
//                    Log.d("BSSID : " + sr.BSSID, TAG);
//                    Log.d("capabilities : " + sr.capabilities, TAG);
//                    Log.d("frequency : " + sr.frequency, TAG);
//                    Log.d("level : " + sr.level, TAG);
//
//                }

            }
        });
    }
    public void store_capture(List<ScanResult> lsr)
    {
        Date currentTime = Calendar.getInstance().getTime();
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        JSONArray json_array;
        JSONObject object;
        int i = 0;
        json_array = new JSONArray();
        for(ScanResult sr : wm.getScanResults())
        {
            Log.d("SSID : "+sr.SSID, TAG);
            Log.d("BSSID : "+sr.BSSID, TAG);
            Log.d("capabilities : "+sr.capabilities, TAG);
            Log.d("frequency : "+sr.frequency, TAG);
            Log.d("level : "+sr.level, TAG);
            try{
                object = new JSONObject();
                object.put("ssid", sr.SSID);
                object.put("bssid", sr.BSSID);
                object.put("capabilities", sr.capabilities);
                object.put("frequency", sr.frequency);
                object.put("level", sr.level);
                object.put("timestamp", currentTime.toString());
                json_array.put(i, object);
                i++;
                } catch (JSONException e) {
                    if(BuildConfig.DEBUG)
                        Log.e(TAG, "Problème avec les données JSON");
                }
        }
        try {
            File dirPublicDocuments =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if(!dirPublicDocuments.mkdirs())
            {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "tous les sous-dossiers" + dirPublicDocuments.getAbsolutePath() +
                            " existent déjà");
                }
            }
            if(BuildConfig.DEBUG)
                Log.d(TAG, dirPublicDocuments.toString());

            File json_file = new File(dirPublicDocuments, FILENAME+ currentTime.toString()+ JSON_EXTENSION);
            fos = new FileOutputStream(json_file);
            osw = new OutputStreamWriter(fos);
            if(BuildConfig.DEBUG) {
                Log.d(TAG, json_array.toString());
                Log.d(TAG, osw.toString());
            }
            osw.write(json_array.toString());
        } catch (FileNotFoundException e) {
            if(BuildConfig.DEBUG)
                Log.e(TAG, "new FileOutputStream()", e);
        } catch (IOException e) {
            if(BuildConfig.DEBUG)
                Log.e(TAG, "problème io");
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    if(BuildConfig.DEBUG)
                        Log.e(TAG, "osw.close()", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    if(BuildConfig.DEBUG)
                        Log.e(TAG, "fos.close()", e);
                }
            }
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
