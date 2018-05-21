package besnier.wifilocator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by clement_besnier on 15/05/2018.
 */

public class FingerprintManager {
    private static final String TAG = FingerprintManager.class.getSimpleName();
    private String filename;
    String JSON_EXTENSION = ".json";


    public FingerprintManager(String filename)
    {
        this.filename = filename;
    }


    static public ArrayList<Fingerprint> loadFingerprints(File folder)
    {
        ArrayList<Fingerprint> lfp = new ArrayList<>();
        if(folder.isDirectory()) {

            for (File f : folder.listFiles()) {
                Fingerprint fp = new Fingerprint();
                fp.load(f);
                lfp.add(fp);
            }
        }
        return lfp;
    }

    static public ArrayList<AnnotatedFingerprint> loadAnnotatedFingerprints(File folder)
    {
        ArrayList<AnnotatedFingerprint> lafp = new ArrayList<>();
        if(folder.isDirectory())
        {
            for(File f : folder.listFiles())
            {
                AnnotatedFingerprint afp = new AnnotatedFingerprint();
                afp.load(f);
                lafp.add(afp);
            }
        }
        return lafp;
    }

    static public Fingerprint loadFingerprint(File file)
    {
        Fingerprint fp = new Fingerprint();
        fp.load(file);
        return fp;
    }



    /**
     * @param lafp list ot annotated fingerprints
     * @param fp fingerprint currently measured
     * @param vb the way beacon signal power may be compared
     * @return res_pos = \frac{\sum_{i=1}^{n} \frac{d_i}{\abs(r - f_i}}{\sum_{i=1}^{n} \frac{1}{\abs{r - f_i}}}
     */
    public Position estimatePosition(List<AnnotatedFingerprint> lafp, Fingerprint fp, VectorizedBeacons vb)
    {
        long distance;
        long overdistance;
        long coefficient = 0;
        Position res_pos = new Position(0, 0, 0);
        fp.vectorizeMeasure(vb);
        for (AnnotatedFingerprint afp : lafp)
        {
            res_pos.add(afp.pos);
            afp.vectorizeMeasure(vb);
//            Log.d(TAG, "vb afp : "+afp.vectorizedMeasure.toArray());
//            Log.d(TAG, "vb fp : "+fp.vectorizedMeasure.toArray());
            Log.d(TAG, "afp" + afp.toStringvectorizedMeasure());
            Log.d(TAG, "fp" + fp.toStringvectorizedMeasure());
            distance = afp.distanceVectorizedMeasure(fp);
            if(distance < 0.005)
            {
                overdistance = 0;
            }
            else
            {
                overdistance = 1/distance;
            }
            res_pos.multiply_scalar(overdistance);
            coefficient += overdistance;

        }
        res_pos.multiply_scalar(1/coefficient);
        return res_pos;
    }

    public String findNearestBeacon(Fingerprint fp)
    {
        if(fp.lbm.size() > 0) {
            BeaconMeasure nearestBeaconMeasure = fp.lbm.get(0);
            for (BeaconMeasure bm : fp.lbm) {
                if (nearestBeaconMeasure.getLevel() < bm.getLevel()) {
                    nearestBeaconMeasure = bm;
                }
            }
            return nearestBeaconMeasure.getSsid();
        }
        else
        {
            return "No beacon detected";
        }
    }

    public void storeFingerprints(Fingerprint fp, File prefix_file)
    {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        JSONObject json_array = fp.toJSON();

        // storing
        try {
//            File dirPublicDocuments =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (!prefix_file.mkdirs()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "tous les sous-dossiers" + prefix_file.getAbsolutePath() +
                            " existent déjà");
                }
            }
            if (BuildConfig.DEBUG)
                Log.d(TAG, prefix_file.toString());
            Date currentTime = Calendar.getInstance().getTime();
            File json_file = new File(prefix_file, filename + currentTime.toString() + JSON_EXTENSION);
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

    public void storeFingerprints(List<Fingerprint> lfp, File prefix)
    {
        for(Fingerprint fp: lfp)
        {
            storeFingerprints(fp, prefix);
        }
    }
}
