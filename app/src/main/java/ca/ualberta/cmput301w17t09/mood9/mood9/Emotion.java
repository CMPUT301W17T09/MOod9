package ca.ualberta.cmput301w17t09.mood9.mood9;

/**
 * Created by dannick on 2/22/17.
 */

public class Emotion {
    private String id;
    private String name;
    private String color;
    private String description;
    private String image_name;

    public Emotion(String id, String name, String color, String description, String image_name) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.image_name = image_name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return image_name;
    }
}
