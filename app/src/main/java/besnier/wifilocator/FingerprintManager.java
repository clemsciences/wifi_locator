package besnier.wifilocator;

import android.os.Environment;
import android.util.Log;

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

    public void loadFingerprints()
    {

    }
    public void findNearestFingerprint()
    {

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
