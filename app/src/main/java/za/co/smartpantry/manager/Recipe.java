package za.co.smartpantry.manager;
public class Recipe {
    public long id;
    public String name, steps;
    public int ingredientCount;
    public Recipe(long id, String name, String steps, int ingredientCount) {
        this.id=id; this.name=name; this.steps=steps; this.ingredientCount=ingredientCount;
    }
}
