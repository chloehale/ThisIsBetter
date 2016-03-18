package s3.thisisbetter;

import com.firebase.client.Firebase;

/**
 * Created by Chloe on 3/10/16.
 */
public class DB {

    static final String EVENTS_KEY = "events";
    static final String USERS_KEY = "users";
    static final String DATES_KEY = "dates";

    static Firebase ref = null;


    public static Firebase getFirebaseRef() {
        if (ref == null) {
            ref = new Firebase("https://this-is-better.firebaseIO.com");
        }

        return ref;
    }

    public static Firebase getEventsRef() {
        return getFirebaseRef().child(EVENTS_KEY);
    }

    public static Firebase getUsersRef() {
        return getFirebaseRef().child(USERS_KEY);
    }

    public static Firebase getDatesRef() {
        return getFirebaseRef().child(DATES_KEY);
    }

    public static String getUID() {
        return getFirebaseRef().getAuth().getUid();
    }
}
