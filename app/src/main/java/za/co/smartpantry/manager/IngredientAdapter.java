package za.co.smartpantry.manager;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.Holder> {
    public interface Listener { void onEdit(Ingredient ingredient); void onDelete(Ingredient ingredient); }
    private final ArrayList<Ingredient> items;
    private final Listener listener;
    public IngredientAdapter(ArrayList<Ingredient> items, Listener listener) { this.items=items; this.listener=listener; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Ingredient i=items.get(position);
        h.name.setText(i.name); h.quantity.setText(i.quantity+" "+i.unit);
        h.expiry.setText(i.expiry==null || i.expiry.trim().isEmpty() ? "Expiry date: not set" : "Expiry date: "+i.expiry);
        h.edit.setOnClickListener(v -> listener.onEdit(i));
        h.delete.setOnClickListener(v -> new AlertDialog.Builder(v.getContext()).setTitle("Delete ingredient?")
            .setMessage("Remove "+i.name+" from your pantry?").setNegativeButton("Cancel", (d,w)->d.dismiss())
            .setPositiveButton("Delete", (d,w)->listener.onDelete(i)).show());
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        TextView name,quantity,expiry; Button edit,delete;
        Holder(View v) { super(v); name=v.findViewById(R.id.txtName); quantity=v.findViewById(R.id.txtQuantity); expiry=v.findViewById(R.id.txtExpiry); edit=v.findViewById(R.id.btnEdit); delete=v.findViewById(R.id.btnDelete); }
    }
}
