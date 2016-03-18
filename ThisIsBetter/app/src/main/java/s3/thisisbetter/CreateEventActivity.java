package s3.thisisbetter;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    public final static String PARENT_TYPE = "create_event";
    private MaterialCalendarView mCalView;
    private EditText mEventTitle;
    private ArrayList<String> proposedDateIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        proposedDateIDs = new ArrayList<>();

        //Change the color of the status bar if the version > lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivity();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelActivity();
            }
        });

        //TODO: Add functionality to the event title input text
        mEventTitle = (EditText) findViewById(R.id.event_title);

        mCalView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mCalView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

    }

    private void goToNextActivity() {
        saveDates();
        String eventTitle = mEventTitle.getText().toString();

        Intent intent = new Intent(CreateEventActivity.this, AvailabilityInputActivity.class);
        intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, PARENT_TYPE);
        intent.putExtra(AppConstants.EXTRA_EVENT_TITLE, eventTitle);
        intent.putExtra(AppConstants.EXTRA_PROPOSED_DATE_IDS, proposedDateIDs);
        startActivity(intent);
    }

    private void saveDates() {
        eraseOldDates();

        Firebase datesRef = DB.getDatesRef();
        List<CalendarDay> dates = mCalView.getSelectedDates();

        for (CalendarDay pickedDate : dates) {
            int day = pickedDate.getDay();
            int month = pickedDate.getMonth();
            int year = pickedDate.getYear();

            TimeBlock timeBlock = new TimeBlock(day, month, year);
            Firebase newDateRef = datesRef.push();

            // Add the data to the new location
            newDateRef.setValue(timeBlock);
            proposedDateIDs.add(newDateRef.getKey());
        }
    }

    private void cancelActivity() {
        eraseOldDates();

        // Close the activity
        finish();
    }

    private void eraseOldDates() {
        // Erase any dates that might've been created in the database
        if (!proposedDateIDs.isEmpty()) {
            Firebase datesRef = DB.getDatesRef();
            Map<String, Object> removeDates = new HashMap<>();

            for(String dateID : proposedDateIDs) {
                removeDates.put(dateID, null);
            }

            datesRef.updateChildren(removeDates);
        }
        proposedDateIDs.clear();
    }

}
