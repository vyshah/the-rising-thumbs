package therisingthumbs.banking;

import android.app.Application;

/**
 * Created by dhart_000 on 11/1/2014.
 */
public class myApplication extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        user = u;
    }
}
