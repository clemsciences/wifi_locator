package besnier.wifilocator.beacons;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import besnier.wifilocator.BuildConfig;
import besnier.wifilocator.fingerprints.Fingerprint;

/**
 * Created by clement_besnier on 18/05/2018.
 */

public class BeaconManager {
    private static final String TAG = BeaconMeasure.class.getSimpleName();
//    private List<Fingerprint> lfp;
//    public BeaconManager()
//    {
//
//
//    }
    static public ArrayList<String> define_beacons(List<Fingerprint> lfp)
    {
        VectorizedBeacons vb = new VectorizedBeacons(lfp);
        return vb.vect_bssid;
    }
    static public void store_beacons(String filename, List<Fingerprint> lfp)
    {
        VectorizedBeacons vb = new VectorizedBeacons(lfp);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        JSONArray json_array = vb.toJSON();

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
            Date currentTime = Calendar.getInstance().getTime();
            File json_file = new File(dirPublicDocuments,
                    filename + currentTime.toString() + ".json");
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
    public static JSONArray load_beacons(Intent data)
    {
        JSONArray objet = new JSONArray();
        Uri uri = data.getData();
        String uriString = uri.toString();
        File dirPublicDocuments =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        File file = new File(dirPublicDocuments.toString(), uri.getPath().split(":")[1]);


        if (uriString.startsWith("content://")) {
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try
            {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                String resultat;
                while ((resultat = br.readLine()) != null)
                {
                    if(sb.length() > 0)
                    {
                        sb.append("\n");
                    }
                    sb.append(resultat);
                }
            }
            catch(IOException e)
            {
                if(BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            }
            finally {
                if(br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch(IOException e)
                    {
                        if(BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
                if(isr != null)
                {
                    try
                    {
                        isr.close();
                    }
                    catch(IOException e)
                    {
                        if(BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
                if(fis != null)
                {
                    try
                    {
                        fis.close();
                    }
                    catch(IOException e)
                    {
                        if(BuildConfig.DEBUG)
                            Log.e(TAG, e.toString());
                    }
                }
            }
            try {
                objet = new JSONArray(sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return objet;
    }
}
