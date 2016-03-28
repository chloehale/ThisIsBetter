package s3.thisisbetter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import s3.thisisbetter.AppConstants;
import s3.thisisbetter.R;

public class CreateEventActivity extends AppCompatActivity {

    public final static String PARENT_TYPE = "create_event";
    private MaterialCalendarView mCalView;
    private EditText mEventTitle;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Change the color of the status bar if the version > lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        mNextButton = (Button) findViewById(R.id.next_button);
        enableNextButton(false);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivity();
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity
                finish();
            }
        });

        //TODO: Add functionality to the event title input text
        mEventTitle = (EditText) findViewById(R.id.event_title);

        mCalView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mCalView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        mCalView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                List<CalendarDay> dates = mCalView.getSelectedDates();
                enableNextButton(dates.size() > 0);
            }
        });
        Calendar c = Calendar.getInstance();
        mCalView.setMinimumDate(c);
        mCalView.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
    }

    private void goToNextActivity() {
        String eventTitle = mEventTitle.getText().toString();

        if (TextUtils.isEmpty(eventTitle)) {
            mEventTitle.setError(getString(R.string.error_field_required));
            mEventTitle.requestFocus();
            return;
        }

        List<CalendarDay> dates = mCalView.getSelectedDates();
        ArrayList<CalendarDay> datesArray = new ArrayList<>();
        for (CalendarDay date : dates) {
            datesArray.add(date);
        }

        Intent intent = new Intent(CreateEventActivity.this, AvailabilityInputActivity.class);
        intent.putExtra(AppConstants.EXTRA_PARENT_TYPE, PARENT_TYPE);
        intent.putExtra(AppConstants.EXTRA_EVENT_TITLE, eventTitle);
        intent.putParcelableArrayListExtra(AppConstants.EXTRA_DATES_ARRAY, datesArray);
        startActivity(intent);
    }

    private void enableNextButton(boolean enabled) {
        if(enabled) {
            mNextButton.setEnabled(true);
            mNextButton.setTextColor(Color.WHITE);
        } else {
            mNextButton.setEnabled(false);
            mNextButton.setTextColor(Color.argb(180, 255, 255, 255));
        }
    }

}
