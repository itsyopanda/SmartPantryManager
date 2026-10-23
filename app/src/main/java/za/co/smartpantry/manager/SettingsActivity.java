package za.co.smartpantry.manager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private CheckBox check; private SharedPreferences prefs;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_settings);
        check=findViewById(R.id.checkExpiry); prefs=getSharedPreferences("settings",MODE_PRIVATE);
        check.setChecked(prefs.getBoolean("expiry_alerts",true));
        findViewById(R.id.btnSaveSettings).setOnClickListener(v -> {
            prefs.edit().putBoolean("expiry_alerts",check.isChecked()).apply();
            Toast.makeText(this,"Settings saved",Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnSettingsBack).setOnClickListener(v -> finish());
    }
}
