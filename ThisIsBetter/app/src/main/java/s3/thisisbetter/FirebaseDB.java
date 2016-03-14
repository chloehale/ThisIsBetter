package s3.thisisbetter;

import com.firebase.client.Firebase;

/**
 * Created by Chloe on 3/10/16.
 */
public class FirebaseDB {

    public static Firebase getFirebaseRef() {
        return new Firebase("https://this-is-better.firebaseIO.com");
    }
}
