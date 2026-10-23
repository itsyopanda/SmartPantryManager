package za.co.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {
    private DatabaseHelper db; private RecyclerView recycler; private TextView empty;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_pantry);
        db=new DatabaseHelper(this); recycler=findViewById(R.id.recyclerPantry); empty=findViewById(R.id.txtEmpty);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btnAdd).setOnClickListener(v -> startActivity(new Intent(this, IngredientFormActivity.class)));
        findViewById(R.id.btnRecipes).setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
    @Override protected void onResume() { super.onResume(); loadPantry(); }
    private void loadPantry() {
        ArrayList<Ingredient> items=db.getPantry();
        empty.setVisibility(items.isEmpty()?View.VISIBLE:View.GONE);
        recycler.setAdapter(new IngredientAdapter(items,new IngredientAdapter.Listener() {
            @Override public void onEdit(Ingredient i) {
                Intent intent=new Intent(PantryActivity.this,IngredientFormActivity.class); intent.putExtra("ingredient_id",i.id); startActivity(intent);
            }
            @Override public void onDelete(Ingredient i) { db.deleteIngredient(i.id); loadPantry(); }
        }));
    }
}
