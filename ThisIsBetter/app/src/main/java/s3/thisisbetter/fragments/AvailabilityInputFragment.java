package s3.thisisbetter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import s3.thisisbetter.R;
import s3.thisisbetter.adapters.AvailabilityInputArrayAdapter;
import s3.thisisbetter.model.TimeBlock;

/**
 * The fragment for the Invited tab
 */
public class AvailabilityInputFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TimeBlock timeBlock;

    public AvailabilityInputFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AvailabilityInputFragment newInstance(int sectionNumber, TimeBlock timeBlock) {
        AvailabilityInputFragment fragment = new AvailabilityInputFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragment.setTimeBlock(timeBlock);
        return fragment;
    }

    public void setTimeBlock(TimeBlock timeBlock) {
        this.timeBlock = timeBlock;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_availability_input, container, false);
        ArrayAdapter<String> adapter = new AvailabilityInputArrayAdapter(rootView.getContext(), timeBlock.generateTimesArray(), timeBlock);
        ListView timesListView = (ListView) rootView.findViewById(R.id.times_list_view);
        timesListView.setAdapter(adapter);
        timesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        timesListView.setItemsCanFocus(false);
        timesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!timeBlock.isAvailable(position) ) {
                    view.setBackgroundResource(R.color.colorSelected);
                    timeBlock.setAvailable(position);
                }
                else {
                    view.setBackgroundResource(R.color.colorLight);
                    timeBlock.setUnavailable(position);
                }
            }
        });


        return rootView;
    }
}
