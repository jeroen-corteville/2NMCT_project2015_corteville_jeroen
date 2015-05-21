package teamtreehouse.com.desopdracht;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Jeroen on 18/04/2015.
 */
public class RibbitApplication extends Application {



    @Override
    public void onCreate() {
        Parse.initialize(this, "0ZDCatcbRi88hPQ9pZM02gV6wmXJDtrGHxqOcFK2", "WoEleFUjlDEPtcjwaQAGlr4hN28sK8h0fq6eAF0w");
        ParseInstallation.getCurrentInstallation().saveInBackground();


    }
}
