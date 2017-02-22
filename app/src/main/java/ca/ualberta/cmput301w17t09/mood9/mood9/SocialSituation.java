package ca.ualberta.cmput301w17t09.mood9.mood9;

/**
 * Created by dannick on 2/22/17.
 */

public class SocialSituation {
    private String id;
    private String name;
    private String description;

    public SocialSituation(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
