package besnier.wifilocator;

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

    static public Fingerprint loadFingerprints(String filename)
    {

        Fingerprint fp_res = new Fingerprint();

        File dirPublicDocuments =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        File file = new File(dirPublicDocuments.toString(), filename.split(":")[1]); //.split(":")[1].split("/")[1]
    //                    File file = new File(uri.getPath()); //.split(":")[1].split("/")[1]
    //                    Log.d(TAG, uriString.split(":")[0]);
    //                    Log.d(TAG, uriString.split(":")[1]);
    //                    myFile.getPath();
    //                    myFile.getAbsolutePath();
    //                    myFile.get
    //                    myFile.getPath()
        String path = file.getAbsolutePath();


        StringBuilder sb = new StringBuilder();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String resultat;
            while ((resultat = br.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(resultat);
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, e.toString());
        } finally
        {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, e.toString());
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, e.toString());
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, e.toString());
                }
            }
            try
            {
                JSONObject object = new JSONObject(sb.toString());

                if(BuildConfig.DEBUG)
                {
                    Log.d(TAG, object.toString());
                }

                fp_res.fromJSON(object);
            }
            catch (JSONException e)
            {
                if(BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            }
        }
        return fp_res;
    }



    /**
     * @param lafp list ot annotated fingerprints
     * @param fp fingerprint currently measured
     * @param vb the way beacon signal power may be compared
     * @return res_pos = \frac{\sum_{i=1}^{n} \frac{d_i}{\abs(r - f_i}}{\sum_{i=1}^{n} \frac{1}{\abs{r - f_i}}}
     */
    public Position estimatePosition(List<AnnotatedFingerprint> lafp, Fingerprint fp, VectorizedBeacons vb)
    {
        fp.vectorizeMeasure(vb);
        long distance;
        long overdistance;
        long coefficient = 0;
        Position res_pos = new Position(0, 0, 0);
        for (AnnotatedFingerprint afp : lafp)
        {
            res_pos.add(afp.pos);
            afp.vectorizeMeasure(vb);
            distance = afp.distanceVectorizedMeasure(fp);
            overdistance = 1/distance;
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

    public void storeFingerprints(Fingerprint fp)
    {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        JSONObject json_array = fp.toJSON();

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
            File json_file = new File(dirPublicDocuments, filename + currentTime.toString() + JSON_EXTENSION);
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

    public void storeFingerprints(List<Fingerprint> lfp)
    {
        for(Fingerprint fp: lfp)
        {
            storeFingerprints(fp);
        }
    }
}
