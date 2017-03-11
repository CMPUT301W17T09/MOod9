package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fmachaal on 2/22/17.
 */

//Sourced from http://abhiandroid.com/ui/custom-spinner-examples.html
    //On Feb 22, 2017

public class EmotionsSpinnerAdapter extends BaseAdapter {
    Context context;
    int emoticons[];
    String[] emotions;
    LayoutInflater inflter;

    public EmotionsSpinnerAdapter(Context applicationContext, int[] emoticons, String[] emotions) {
        this.context = applicationContext;
        this.emoticons = emoticons;
        this.emotions = emotions;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return emoticons.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.emotion_spinner_layout, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(emoticons[i]);
        names.setText(emotions[i]);
        return view;
    }
}