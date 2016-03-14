package s3.thisisbetter;

import com.firebase.client.Firebase;

/**
 * Created by Chloe on 3/10/16.
 */
public class ThisIsBetterApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
