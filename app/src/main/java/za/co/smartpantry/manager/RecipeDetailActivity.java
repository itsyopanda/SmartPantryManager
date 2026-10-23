package za.co.smartpantry.manager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_recipe_detail);
        long id=getIntent().getLongExtra("recipe_id",-1);
        ((TextView)findViewById(R.id.txtRecipeTitle)).setText(getIntent().getStringExtra("recipe_name"));
        ((TextView)findViewById(R.id.txtRecipeSteps)).setText(getIntent().getStringExtra("recipe_steps"));
        DatabaseHelper db=new DatabaseHelper(this);
        ((TextView)findViewById(R.id.txtRecipeIngredients)).setText(db.getRecipeIngredients(id));
        findViewById(R.id.btnDetailBack).setOnClickListener(v -> finish());
    }
}
