package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by cdkushni on 3/5/17.
 * code reference from https://www.caveofprogramming.com/guest-posts/custom-listview-with-imageview-and-textview-in-android.html
 */

public class MoodListAdapter extends BaseAdapter {
    ArrayList<String> result;
    Context context;
    ArrayList<Integer> imageId;
    private static LayoutInflater inflater=null;
    public MoodListAdapter(FeedActivity feedActivity, ArrayList<String> usrNameList, ArrayList<Integer> emoteImages) {
        result = usrNameList;
        context = feedActivity;
        imageId = emoteImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return result.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.fragment_mood, null);
        holder.tv=(TextView) rowView.findViewById(R.id.username);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView);
        holder.tv.setText(result.get(position));
        holder.img.setImageResource(imageId.get(position));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+ result.get(position), Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}
