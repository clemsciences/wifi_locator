package besnier.wifilocator;

import android.util.Log;

import org.json.JSONObject;

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

    static public boolean exportFingerprints(File folder_src, File folder_dst)
    {
        boolean success = true;
        if(!folder_dst.mkdirs())
        {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "tous les sous-dossiers" + folder_dst.getAbsolutePath() +
                        " existent déjà");
            }
        }
        Log.e(TAG, "chemin absolu : "+folder_src.getAbsolutePath());
        Log.e(TAG, "chemin : "+folder_src.getPath());
        Log.e(TAG, "Dossier ? : "+folder_src.isDirectory());
        Log.e(TAG, folder_src.getAbsolutePath());
        for(File f : folder_src.listFiles()) {
            String filename = f.getName();
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(f);
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
                success = false;
                if (BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
                    }
                }
            }

            File copied_file = new File(folder_dst, filename);
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;
            try {
                fos = new FileOutputStream(copied_file);

                osw = new OutputStreamWriter(fos);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, sb.toString());
                    Log.d(TAG, osw.toString());
                }
                osw.write(sb.toString());
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            } finally {
                if (osw != null) {
                    try {
                        osw.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, "osw.close()", e);
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, "fos.close()", e);
                    }
                }
            }
        }
        return success;
    }

    static public boolean importFingerprints(File folder_src, File folder_dst)
    {
        boolean success = true;

        if(!folder_dst.mkdirs())
        {
            for(File f : folder_dst.listFiles())
            {
                boolean delete = f.delete();
                if(!delete)
                {
                    Log.e(TAG, "problem with deleting files");
                }
            }
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "tous les sous-dossiers" + folder_dst.getAbsolutePath() +
                        " existent déjà et ont été supprimés");
            }
        }
        Log.e(TAG, "chemin absolu : "+folder_src.getAbsolutePath());
        Log.e(TAG, "chemin : "+folder_src.getPath());
        Log.e(TAG, "Dossier ? : "+folder_src.isDirectory());
        Log.e(TAG, folder_src.getAbsolutePath());
        for(File f : folder_src.listFiles()) {
            String filename = f.getName();
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(f);
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
                success = false;
                if (BuildConfig.DEBUG)
                    Log.e(TAG, e.toString());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG) Log.e(TAG, e.toString());
                    }
                }
            }

            File copied_file = new File(folder_dst, filename);
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;
            try {
                fos = new FileOutputStream(copied_file);

                osw = new OutputStreamWriter(fos);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, sb.toString());
                    Log.d(TAG, osw.toString());
                }
                osw.write(sb.toString());
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            } finally {
                if (osw != null) {
                    try {
                        osw.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, "osw.close()", e);
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        success = false;
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, "fos.close()", e);
                    }
                }
            }
        }
        return success;
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
    public String estimateLocation(List<Fingerprint> lfp, Fingerprint fp_to_locate, VectorizedBeacons vb)
    {
        long distance;
        long mini_distance = Long.MAX_VALUE;
        int i_mini_distance = 0;
        int i = 0;
//        ArrayList<Long> distances = new ArrayList<>();
        fp_to_locate.vectorizeMeasure(vb);
        for(Fingerprint fp : lfp)
        {
            fp.vectorizeMeasure(vb);
            distance = fp.distanceVectorizedMeasure(fp_to_locate);
            if(distance < mini_distance)
            {
                mini_distance = distance;
                i_mini_distance = i;
            }
            i ++;
        }
        return lfp.get(i_mini_distance).getLocation();
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
