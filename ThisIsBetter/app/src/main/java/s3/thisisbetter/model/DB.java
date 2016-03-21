package s3.thisisbetter.model;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Chloe on 3/10/16.
 */
public class DB {

    static final String EVENTS_KEY = "events";
    static final String USERS_KEY = "users";
    static final String DATES_KEY = "dates";

    static Firebase ref = null;

    static Map<Query, ChildEventListener> childListeners = null;


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

    public static String getMyUID() {
        return getFirebaseRef().getAuth().getUid();
    }

    public static void monitorChildListener(Query ref, ChildEventListener listener) {
        if(childListeners == null) {
            childListeners = new HashMap<>();
        }

        childListeners.put(ref, listener);
    }

    public static void removeAllListeners() {
        Iterator iter = childListeners.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            Query ref = (Query) pair.getKey();
            ChildEventListener listener = (ChildEventListener) pair.getValue();
            ref.removeEventListener(listener);
        }
    }
}
