package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.Date;

/**
 * Created by dannick on 2/22/17.
 */

public class ElasticSearchTask<T> {
    public enum Types { PUT, DELETE };

    private T t;
    private Types type;
    private Date created;
    private Date lastTried;
}
