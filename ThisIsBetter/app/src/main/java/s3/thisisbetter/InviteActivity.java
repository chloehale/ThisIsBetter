package s3.thisisbetter;

import android.app.ListActivity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class InviteActivity extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<TeamMember> listItems=new ArrayList<>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    CustomInviteList adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter=0;

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomInviteList(InviteActivity.this,listItems);
        listView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable fabDr= fab.getDrawable();
        DrawableCompat.setTint(fabDr, Color.WHITE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItems(view);
            }
        });


    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
        String name = "Kyle Collinsworth";
        String URL = "https://pbs.twimg.com/profile_images/671019522237730816/pWsxY_iz.jpg";
        if (clickCounter++ % 2 == 1)
        {
            name = "Nick Emery";
            URL = "https://pbs.twimg.com/profile_images/668054913658687488/OkAmPqjX.jpg";
        }
        listItems.add(new TeamMember(name, URL));
        adapter.add(new TeamMember(name, URL));
        adapter.notifyDataSetChanged();
    }



}
