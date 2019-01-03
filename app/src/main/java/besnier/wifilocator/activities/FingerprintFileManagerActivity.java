package besnier.wifilocator.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import besnier.wifilocator.R;

public class FingerprintFileManagerActivity extends AppCompatActivity {
    private static final String TAG = FingerprintFileManagerActivity.class.getSimpleName();

    ListView liste_fichiers_vue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_file_manager);

        liste_fichiers_vue = findViewById(R.id.liste_fichiers);
        File baseFolder;

        String dossier = getIntent().getStringExtra("dossier");
        if(dossier != null)
        {
            baseFolder = new File(getBaseContext().getFilesDir(), dossier);
        }
        else
        {
            baseFolder = getBaseContext().getFilesDir();
        }



        Log.d(TAG, "on prend la liste de fichiers ici "+baseFolder);
        for(File fileInDirectory : baseFolder.listFiles())
        {
            Log.d(TAG, fileInDirectory.getPath()+" : "+fileInDirectory.getName());
        }
//            Log.d(TAG, "on prend la liste de fichiers ici "+getBaseContext().getFilesDir().listFiles().toString());


        ArrayList<File> liste_fichiers = new ArrayList<>();
        for(File fileInDirectory : baseFolder.listFiles())
        {
            Log.d(TAG, fileInDirectory.getPath()+" : "+fileInDirectory.getName());
            liste_fichiers.add(fileInDirectory);
        }

        final ArrayAdapter<File> adapter = new FileAdapter(FingerprintFileManagerActivity.this, liste_fichiers);
        liste_fichiers_vue.setAdapter(adapter);
        liste_fichiers_vue.setClickable(true);
        liste_fichiers_vue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File touchedFile = (File)  liste_fichiers_vue.getItemAtPosition(position);
                if(touchedFile.isDirectory())
                {
                    Intent i = new Intent(FingerprintFileManagerActivity.this, FingerprintFileManagerActivity.class);
                    i.putExtra("dossier", touchedFile.getName());
                    startActivity(i);
                }


                Log.d(TAG, "On peut appuyer dessus");
            }
        });


    }
}

class FileAdapter extends ArrayAdapter<File>
{
    public FileAdapter(Context context, ArrayList<File> files)
    {
        super(context, 0, files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_filename, parent, false);
        }
        MiniEntreeVueTeneur vue_teneur = (MiniEntreeVueTeneur) convertView.getTag();
        if(vue_teneur == null)
        {
            vue_teneur = new MiniEntreeVueTeneur();
            vue_teneur.mini_date_vue = convertView.findViewById(R.id.mini_date_vue);
            vue_teneur.mini_prefix_vue = convertView.findViewById(R.id.mini_prefix_vue);
            vue_teneur.mini_chemin_vue = convertView.findViewById(R.id.mini_chemin_vue);
            convertView.setTag(vue_teneur);
        }

        File file = getItem(position);

        vue_teneur.mini_date_vue.setText(new Date(file.lastModified()).toString());
        vue_teneur.mini_chemin_vue.setText(file.getParent().toString());
        vue_teneur.mini_prefix_vue.setText(file.getName().toString());


        return convertView;
    }

    private class MiniEntreeVueTeneur
    {
        public TextView mini_date_vue;
        public TextView mini_prefix_vue;
        public TextView mini_chemin_vue;
    }

}
