package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Custom adapter for the feed list, inflates the list fragment items into the list view
 * <p>
 * Created by cdkushni on 3/5/17.
 * code reference from https://www.caveofprogramming.com/guest-posts/custom-listview-with-imageview-and-textview-in-android.html
 * Changed by cdkushni on 3/8/17 to use ArrayLists instead of primitive arrays for compatibility reasons.
 * Changed by cdkushni on 3/10/17 to use a linked list of moods rather than primitive arrays for better encapsulation.
 * </p>
 *
 * @author cdkushni
 * @version 1.2
 * @see FeedActivity
 * @since 1.0
 */

public class MoodListAdapter extends BaseAdapter{
    Context context;
    private LinkedList<Mood> moodList;
    private Mood9Application mApplication;
    private static LayoutInflater inflater=null;
    MoodListAdapter(FeedActivity feedActivity, LinkedList<Mood> moodLinkedList, Mood9Application mApplication) {
        moodList = moodLinkedList;
        context = feedActivity;
        this.mApplication = mApplication;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return moodList.size();
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
        TextView idtv;
        TextView timetv;
        ImageView img;
    }

    /**
     * Inflates each list item view with holders: username, time and emoticon image
     * @param position : list item position
     * @param convertView : the view
     * @param parent : view parent
     * @return rowView
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.fragment_mood, null);
        holder.idtv=(TextView) rowView.findViewById(R.id.username);
        holder.timetv=(TextView) rowView.findViewById(R.id.time);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView);

        holder.idtv.setText(UserModel.getUserProfile(moodList.get(position).getUser_id()).getName());
        holder.timetv.setText(moodList.get(position).getDate());
        String imgNameBuilder = mApplication.getEmotionModel().getEmotion(moodList.get(position).getEmotionId()).getName().toLowerCase() + ".png";
        holder.img.setImageResource(context.getResources().getIdentifier(imgNameBuilder.substring(0, imgNameBuilder.lastIndexOf(".")), "drawable", context.getPackageName()));
        return rowView;
    }

}
