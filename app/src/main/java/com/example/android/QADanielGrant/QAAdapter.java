package com.example.android.QADanielGrant;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Class that acts as the adapter for QAActivity
 */
public class QAAdapter extends BaseAdapter {

    private Context context;
    private String[] questions;
    private static LayoutInflater inflater=null;
    private String category;
    private byte[] categoryImage;
    private DataSnapshot[] data;

    /**
     * Constructor for QAAdapter
     * @param activity the context
     * @param question string array containing all the questions related to a category
     * @param category the given category
     * @param image the image of the given category
     * @param snap snapshot used to get the question string
     */
    public QAAdapter(Context activity, String[] question, String category, byte[] image, DataSnapshot[] snap){
         context = activity;
         questions = question;
         data = snap;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.category = category;
        categoryImage = image;
    }

    @Override
    public int getCount() {
        return questions.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Object bean used to hold the information
     */
    public class Holder
    {
        TextView tv;
    }

    /**
     * Overrid of the getView method to get the view instantiated correctly
     * @param position the position
     * @param convertView the view changed
     * @param parent the parent view
     * @return the view once it has been correctly instantiated
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView = convertView;
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.program_list, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.tv.setText(questions[position]);
            rowView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
            holder.tv.setText(questions[position]);
        }

        //Set the onclick to go to QAActivity
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, QuestionActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("questionNum",  data[position].getKey());
                Log.e("bytesNull", "? " + categoryImage);
                intent.putExtra("imgBytes", categoryImage);
                context.startActivity(intent);

            }
        });
        return rowView;
    }
}
