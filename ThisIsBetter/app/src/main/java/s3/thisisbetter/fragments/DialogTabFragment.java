package s3.thisisbetter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import s3.thisisbetter.R;
import s3.thisisbetter.model.AvailabilityBlock;
import s3.thisisbetter.model.DB;
import s3.thisisbetter.model.User;

/**
 * Created by steve on 3/29/16.
 */
public class DialogTabFragment extends Fragment
{
    private Set<String> userIDs;
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayAdapter adapter;

    private String type;

    public static final String AVAILABLE = "available";
    public static final String NOT_AVAILABLE = "not_available";
    public static final String NOT_RESPONDED = "not_responded";

    private String title;

    public static DialogTabFragment newInstance(Set<String> userIDs, String type) {
        DialogTabFragment dialogTabFragment = new DialogTabFragment();
        dialogTabFragment.setUserIDs(userIDs);
        dialogTabFragment.setType(type);
        return dialogTabFragment;
    }

    private void setUserIDs(Set<String> userIDs) {
        this.userIDs = userIDs;
    }

    private void setType(String type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_dialog_tab, container, false);

        TextView dialogStatusText = (TextView) rootView.findViewById(R.id.dialog_status_text);
        if (type.equals(NOT_AVAILABLE) && userIDs.size() == 0) {
            dialogStatusText.setText("Everyone who has responded is available");
            dialogStatusText.setVisibility(View.VISIBLE);
        }

        if (type.equals(NOT_RESPONDED) && userIDs.size() == 0) {
            dialogStatusText.setText("Everyone has responded");
            dialogStatusText.setVisibility(View.VISIBLE);
        }

        ListView dialogTabListView = (ListView) rootView.findViewById(R.id.dialog_tab_listview);
        adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, usernames);
        dialogTabListView.setAdapter(adapter);

        getUsernames();

        return rootView;
    }

    private void getUsernames() {
        Query queryRef = DB.getUsersRef();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String userID : userIDs) {
                    DataSnapshot userSnapshot = dataSnapshot.child(userID).child(User.EMAIL_KEY);
                    String username = userSnapshot.getValue(String.class);
                    if (!usernames.contains(username)) {
                        usernames.add(username);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Error retrieving users");
            }
        });
    }
}
