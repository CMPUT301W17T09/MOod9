package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rohit on 2017-03-25.
 */

public class CustomRequestAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> request_list = new ArrayList<String>();
    private Context context;
    private String current_user;

    public CustomRequestAdapter(ArrayList<String> request_list, Context context, String current_user) {
        this.request_list = request_list;
        this.context = context;
        this.current_user = current_user;
    }

    @Override
    public int getCount() {
        return request_list.size();
    }

    @Override
    public Object getItem(int position) {
        return request_list.get(position);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.request_fragment, null);
        }

        TextView itemText = (TextView) view.findViewById(R.id.TextView9);
        itemText.setText(request_list.get(position));

        ImageButton accept = (ImageButton) view.findViewById(R.id.accept1);
        ImageButton decline = (ImageButton) view.findViewById(R.id.decline1);

        accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Add 2nd user to current user's followee list, remove current user from 2nd
                //user's requests,notify array adapter
                User current1 = UserModel.getUserProfile(current_user);
                User second = UserModel.getUserProfile(UserModel.getUserID(request_list.get(position)));
                second.addFollowee(current1); //second user is following current user
                current1.removeFromRequests(second.getId());
                UserModel.updateUser(current1);
                UserModel.updateUser(second);
                request_list.remove(position);
                notifyDataSetChanged();
            }
        });

        decline.setOnClickListener(new View.OnClickListener(){
            @Override
            //Same as accept, but do not add 2nd user to current user's followee list
            public void onClick(View v){
                User second = UserModel.getUserProfile(UserModel.getUserID(request_list.get(position)));
                User current1 = UserModel.getUserProfile(current_user);
                current1.removeFromRequests(second.getId());
                request_list.remove(position);
                UserModel.updateUser(current1);
                UserModel.updateUser(second);
                notifyDataSetChanged();
            }
        });

        return view;


    }
}
