package za.co.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private DatabaseHelper db; private RecyclerView recycler; private TextView empty;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_suggested_recipes);
        db=new DatabaseHelper(this); recycler=findViewById(R.id.recyclerRecipes); empty=findViewById(R.id.txtNoRecipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
    @Override protected void onResume() { super.onResume(); loadRecipes(); }
    private void loadRecipes() {
        ArrayList<Recipe> recipes=db.getSuggestedRecipes();
        empty.setVisibility(recipes.isEmpty()?View.VISIBLE:View.GONE);
        recycler.setAdapter(new RecipeAdapter(recipes, recipe -> {
            Intent intent=new Intent(this,RecipeDetailActivity.class); intent.putExtra("recipe_id",recipe.id); intent.putExtra("recipe_name",recipe.name); intent.putExtra("recipe_steps",recipe.steps); startActivity(intent);
        }));
    }
}
