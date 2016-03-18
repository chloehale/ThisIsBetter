package s3.thisisbetter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by psoder3 on 3/17/16.
 */
public class CustomInviteList extends ArrayAdapter<TeamMember> {

    private final Activity context;
    public final ArrayList<TeamMember> members;
    public CustomInviteList(Activity context, ArrayList<TeamMember> members) {
        super(context, R.layout.fragment_list_single);
        this.context = context;
        this.members = members;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(members.get(position).name);

        //ImageView imageView = (ImageView) rowView.findViewById(R.id.imgView);
        String urlStr = members.get(position).pictureURL;

        /*
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bmp);
        */


        WebView web = (WebView) rowView.findViewById(R.id.webView);
        web.loadDataWithBaseURL(urlStr,
                "<img src=\"" + urlStr + "\" width=\"100%\"/>", "text/html", "utf-8", null);


        //imageView.setImageResource(new Integer(1));
        return rowView;
    }
}