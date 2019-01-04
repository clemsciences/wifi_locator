package besnier.wifilocator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class MeasureContentActivity extends AppCompatActivity {

    EditText fileEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_content);

        fileEntry = findViewById(R.id.fileEntry);

        String data = getIntent().getStringExtra("data");
        if(data != null)
        {
            fileEntry.setText(data);
        }


    }
}
