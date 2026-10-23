package za.co.smartpantry.manager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class IngredientFormActivity extends AppCompatActivity {
    private DatabaseHelper db; private EditText name,quantity,unit,expiry; private long ingredientId=-1;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_ingredient_form);
        db=new DatabaseHelper(this); name=findViewById(R.id.editName); quantity=findViewById(R.id.editQuantity);
        unit=findViewById(R.id.editUnit); expiry=findViewById(R.id.editExpiry);
        ingredientId=getIntent().getLongExtra("ingredient_id",-1);
        if(ingredientId!=-1) {
            TextView title=findViewById(R.id.txtFormTitle); title.setText("Edit ingredient");
            Ingredient i=db.getIngredient(ingredientId);
            if(i!=null) { name.setText(i.name); quantity.setText(String.valueOf(i.quantity)); unit.setText(i.unit); expiry.setText(i.expiry); }
        }
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }
    private void save() {
        String n=name.getText().toString().trim(), q=quantity.getText().toString().trim(), u=unit.getText().toString().trim(), e=expiry.getText().toString().trim();
        if(n.isEmpty()) { name.setError("Enter an ingredient name"); name.requestFocus(); return; }
        if(q.isEmpty()) { quantity.setError("Enter a quantity"); quantity.requestFocus(); return; }
        double amount;
        try { amount=Double.parseDouble(q); } catch(NumberFormatException ex) { quantity.setError("Enter a valid number"); return; }
        if(amount<=0) { quantity.setError("Quantity must be greater than zero"); return; }
        if(u.isEmpty()) { unit.setError("Enter a unit, e.g. pieces, g or ml"); unit.requestFocus(); return; }
        if(ingredientId==-1) db.addIngredient(n,amount,u,e); else db.updateIngredient(ingredientId,n,amount,u,e);
        Toast.makeText(this,"Ingredient saved",Toast.LENGTH_SHORT).show(); finish();
    }
}
