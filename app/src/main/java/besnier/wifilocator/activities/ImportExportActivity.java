package besnier.wifilocator.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import besnier.wifilocator.R;
import besnier.wifilocator.fingerprints.FingerprintManager;

public class ImportExportActivity extends AppCompatActivity {
    private static final String TAG = ImportExportActivity.class.getSimpleName();

    private EditText entree_prefix;

    private Button bouton_exporter;
    private Button bouton_importer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        bouton_exporter = findViewById(R.id.bouton_exporter);
        bouton_importer = findViewById(R.id.bouton_importer);

        final CharSequence exportation_success_text = "Données exportées dans le dossier Documents";
        final CharSequence exportation_failure_no_prefix_text = "Il faut préciser un préfixe valide";
        final CharSequence exportation_failure_unvalid_prefix_text = "Le préfixe donné n'est pas valide";

        entree_prefix = findViewById(R.id.entree_prefix);

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

        bouton_importer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getApplicationContext();

                int duration = Toast.LENGTH_SHORT;

                if(!entree_prefix.getText().toString().equals("")) {
                    File dirPublicDocuments =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

                    File folder_to_copy = new File(dirPublicDocuments.toString(), entree_prefix.getText().toString());
                    if (folder_to_copy.isDirectory())
                    {

                        File new_folder = new File(getFilesDir(), entree_prefix.getText().toString());

                        boolean success = FingerprintManager.exportFingerprints(folder_to_copy, new_folder);
                        if (success) {
                            Toast.makeText(context, exportation_success_text, duration).show();
                        } else {
                            Toast.makeText(context, "Problème dans l'importation pour des raisons inconnues", duration).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Problème dans l'importation dans les suppressions", duration).show();
                    }
                }
                else
                {
                    Toast.makeText(context, exportation_failure_no_prefix_text, duration).show();
                }
            }
        });
    }





}
