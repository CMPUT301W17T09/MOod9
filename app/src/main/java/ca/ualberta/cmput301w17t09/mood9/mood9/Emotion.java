package ca.ualberta.cmput301w17t09.mood9.mood9;

/**
 * This is the emotions used in the moods.
 * @author CMPUT301W17T09
 * Created by dannick on 2/22/17.
 */

public class Emotion {
    private String id;
    private String name;
    private String color;
    private String description;
    private String imageName;
    private String statsMessage;

    public Emotion(){

    }

    public Emotion(String id, String name, String color, String description, String imageName) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.imageName = imageName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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
        return imageName;
    }

    public String getStatsMessage() {
        return statsMessage;
    }

    public void setStatsMessage(String statsMessage) {
        this.statsMessage = statsMessage;
    }
}
