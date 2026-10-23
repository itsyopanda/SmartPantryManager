package za.co.smartpantry.manager;

import  android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, normalized_name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, name TEXT NOT NULL, normalized_name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        long recipeCount =
                android.database.DatabaseUtils.queryNumEntries(db, "recipes");

        if (recipeCount == 0) {
            seedRecipes(db);
        }
    }

    public static String normalize(String value) {
        String s = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9 ]", "").replaceAll("\\s+", " ");
        if (s.endsWith("ies") && s.length() > 4) s = s.substring(0, s.length()-3) + "y";
        else if (s.endsWith("oes") && s.length() > 4) s = s.substring(0, s.length()-2);
        else if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 3) s = s.substring(0, s.length()-1);
        return s;
    }

    public long addIngredient(String name, double quantity, String unit, String expiry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("normalized_name", normalize(name)); v.put("quantity", quantity);
        v.put("unit", unit.trim()); v.put("expiry", expiry == null ? "" : expiry.trim());
        return db.insert("pantry", null, v);
    }

    public int updateIngredient(long id, String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("normalized_name", normalize(name)); v.put("quantity", quantity);
        v.put("unit", unit.trim()); v.put("expiry", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().update("pantry", v, "id=?", new String[]{String.valueOf(id)});
    }
    public int deleteIngredient(long id) { return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)}); }

    public ArrayList<Ingredient> getPantry() {
        ArrayList<Ingredient> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry ORDER BY name COLLATE NOCASE", null)) {
            while (c.moveToNext()) list.add(new Ingredient(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getString(4)));
        }
        return list;
    }

    public Ingredient getIngredient(long id) {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry WHERE id=?", new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) return new Ingredient(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getString(4));
        }
        return null;
    }

    public ArrayList<Recipe> getSuggestedRecipes() {
        ArrayList<Recipe> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor recipes = db.rawQuery("SELECT id,name,steps FROM recipes ORDER BY name", null)) {
            while (recipes.moveToNext()) {
                long recipeId = recipes.getLong(0);
                boolean canMake = true;
                int count = 0;
                try (Cursor req = db.rawQuery("SELECT normalized_name,quantity,unit FROM recipe_ingredients WHERE recipe_id=?", new String[]{String.valueOf(recipeId)})) {
                    while (req.moveToNext()) {
                        count++;
                        String requiredName = req.getString(0);
                        double requiredQty = req.getDouble(1);
                        String requiredUnit = req.getString(2);
                        boolean found = false;
                        try (Cursor pantry = db.rawQuery("SELECT quantity,unit FROM pantry WHERE normalized_name=?", new String[]{requiredName})) {
                            while (pantry.moveToNext()) {
                                String pantryUnit = pantry.getString(1);
                                if (sameUnit(requiredUnit, pantryUnit) && pantry.getDouble(0) >= requiredQty) {
                                    found = true; break;
                                }
                            }
                        }
                        if (!found) { canMake = false; break; }
                    }
                }
                if (canMake) results.add(new Recipe(recipeId, recipes.getString(1), recipes.getString(2), count));
            }
        }
        return results;
    }

    private boolean sameUnit(String a, String b) {
        String x = a.trim().toLowerCase(Locale.ROOT), y = b.trim().toLowerCase(Locale.ROOT);
        if (x.equals(y)) return true;
        if ((x.equals("piece") || x.equals("pieces") || x.equals("pc") || x.equals("pcs")) &&
            (y.equals("piece") || y.equals("pieces") || y.equals("pc") || y.equals("pcs"))) return true;
        if ((x.equals("g") || x.equals("gram") || x.equals("grams")) && (y.equals("g") || y.equals("gram") || y.equals("grams"))) return true;
        if ((x.equals("ml") || x.equals("millilitre") || x.equals("millilitres")) && (y.equals("ml") || y.equals("millilitre") || y.equals("millilitres"))) return true;
        return false;
    }

    public String getRecipeIngredients(long recipeId) {
        StringBuilder out = new StringBuilder();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT name,quantity,unit FROM recipe_ingredients WHERE recipe_id=? ORDER BY id", new String[]{String.valueOf(recipeId)})) {
            while (c.moveToNext()) out.append("• ").append(c.getString(0)).append(" — ").append(formatQty(c.getDouble(1))).append(" ").append(c.getString(2)).append("\n");
        }
        return out.toString().trim();
    }

    private String formatQty(double n) { return n == (long)n ? String.valueOf((long)n) : String.valueOf(n); }


    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Toast",
                "1. Toast the bread.\n2. Slice the tomato.\n3. Place tomato on toast and season.",
                new String[][]{{"bread","2","pieces"},{"tomato","1","pieces"},{"salt","1","g"}});

        addRecipe(db, "Cheese Toastie",
                "1. Put cheese between bread slices.\n2. Toast in a pan until golden and melted.",
                new String[][]{{"bread","2","pieces"},{"cheese","2","slices"}});

        addRecipe(db, "Banana Oat Bowl",
                "1. Mash the banana.\n2. Mix with oats and milk.",
                new String[][]{{"banana","1","pieces"},{"oats","50","g"},{"milk","100","ml"}});

        addRecipe(db, "Scrambled Eggs",
                "1. Beat the eggs.\n2. Melt butter in a pan.\n3. Stir eggs gently until cooked.",
                new String[][]{{"egg","2","pieces"},{"butter","5","g"},{"salt","1","g"}});

        addRecipe(db, "Simple Pancakes",
                "1. Mix flour, milk and egg into a smooth batter.\n2. Cook small portions in a lightly greased pan.",
                new String[][]{{"flour","100","g"},{"milk","150","ml"},{"egg","1","pieces"}});

        addRecipe(db, "Garlic Pasta",
                "1. Boil pasta until tender.\n2. Fry garlic in oil.\n3. Toss pasta with garlic oil.",
                new String[][]{{"pasta","100","g"},{"garlic","1","pieces"},{"oil","10","ml"}});

        addRecipe(db, "Peanut Butter Sandwich",
                "1. Spread peanut butter over bread.\n2. Close sandwich and serve.",
                new String[][]{{"bread","2","pieces"},{"peanut butter","20","g"}});

        addRecipe(db, "Apple Oat Porridge",
                "1. Cook oats in milk.\n2. Chop apple and add on top.",
                new String[][]{{"oats","50","g"},{"milk","150","ml"},{"apple","1","pieces"}});

        addRecipe(db, "Cheesy Pasta",
                "1. Boil pasta.\n2. Drain, add cheese and stir until melted.",
                new String[][]{{"pasta","100","g"},{"cheese","30","g"}});

        addRecipe(db, "Tomato Egg Scramble",
                "1. Chop tomato.\n2. Cook tomato briefly.\n3. Add beaten eggs and scramble.",
                new String[][]{{"tomato","1","pieces"},{"egg","2","pieces"},{"oil","5","ml"}});

        addRecipe(db, "Honey Banana Toast",
                "1. Toast bread.\n2. Slice banana and place on toast.\n3. Drizzle with honey.",
                new String[][]{{"bread","2","pieces"},{"banana","1","pieces"},{"honey","10","g"}});

        addRecipe(db, "Garlic Butter Bread",
                "1. Mix softened butter with chopped garlic.\n2. Spread on bread and toast.",
                new String[][]{{"bread","2","pieces"},{"butter","10","g"},{"garlic","1","pieces"}});

        addRecipe(db, "Fruit Milk Smoothie",
                "1. Peel banana and chop apple.\n2. Blend fruit with milk until smooth.",
                new String[][]{{"banana","1","pieces"},{"apple","1","pieces"},{"milk","200","ml"}});

        addRecipe(db, "Flour Flatbread",
                "1. Mix flour, water and salt into dough.\n2. Roll thin and cook in a hot pan.",
                new String[][]{{"flour","100","g"},{"water","60","ml"},{"salt","1","g"}});

        addRecipe(db, "Omelette",
                "1. Beat eggs with salt.\n2. Cook in a pan until set and fold.",
                new String[][]{{"egg","2","pieces"},{"salt","1","g"},{"oil","5","ml"}});

        addRecipe(db, "Tomato Cheese Sandwich",
                "1. Slice tomato and cheese.\n2. Place between bread and serve or toast.",
                new String[][]{{"bread","2","pieces"},{"tomato","1","pieces"},{"cheese","1","slices"}});

        addRecipe(db, "Sweet Oat Bowl",
                "1. Warm milk and stir in oats.\n2. Add honey and mix.",
                new String[][]{{"oats","50","g"},{"milk","150","ml"},{"honey","10","g"}});

        addRecipe(db, "Apple Peanut Butter Bites",
                "1. Slice apple into wedges.\n2. Serve with peanut butter for dipping.",
                new String[][]{{"apple","1","pieces"},{"peanut butter","15","g"}});

        addRecipe(db, "Simple Boiled Eggs",
                "1. Place eggs in boiling water.\n2. Boil for 8–10 minutes and cool.",
                new String[][]{{"egg","2","pieces"},{"water","500","ml"}});

        addRecipe(db, "Tomato Garlic Pasta",
                "1. Boil pasta.\n2. Cook chopped tomato and garlic in oil.\n3. Mix sauce with pasta.",
                new String[][]{{"pasta","100","g"},{"tomato","2","pieces"},{"garlic","1","pieces"},{"oil","10","ml"}});
    }


    private void addRecipe(SQLiteDatabase db, String name, String steps, String[][] ingredients) {
        ContentValues rv = new ContentValues(); rv.put("name", name); rv.put("steps", steps);
        long id = db.insert("recipes", null, rv);
        for (String[] item : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", id); iv.put("name", item[0]); iv.put("normalized_name", normalize(item[0]));
            iv.put("quantity", Double.parseDouble(item[1])); iv.put("unit", item[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }
    public void updateRecipeInstructions() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            seedRecipes(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
