package za.co.smartpantry.manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {
    public interface Listener { void onRecipeClick(Recipe recipe); }
    private final ArrayList<Recipe> items; private final Listener listener;
    public RecipeAdapter(ArrayList<Recipe> items, Listener listener) { this.items=items; this.listener=listener; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe,parent,false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h,int position) {
        Recipe r=items.get(position); h.name.setText(r.name);
        h.info.setText(r.ingredientCount+" required ingredients • Tap to view method");
        h.itemView.setOnClickListener(v -> listener.onRecipeClick(r));
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        TextView name,info;
        Holder(View v) { super(v); name=v.findViewById(R.id.txtRecipeName); info=v.findViewById(R.id.txtRecipeInfo); }
    }
}
