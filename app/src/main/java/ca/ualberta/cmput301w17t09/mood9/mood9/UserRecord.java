package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.Date;

/**
 * Created by dannick on 2/22/17.
 */

public class UserRecord {
    private User user;
    private Date lastUpdated;

    public UserRecord(User user, Date lastUpdated) {
        this.user = user;
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
