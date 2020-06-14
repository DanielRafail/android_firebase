package com.example.android.QADanielGrant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static android.content.ContentValues.TAG;

/**
 * Adapter for the main activity
 */
public class MainActivityAdapter extends BaseAdapter{
    private Bitmap[] image;
    private Context context;
    private String[] category;
    private static LayoutInflater inflater=null;

    /**
     * Constructor for the adapter
     * @param mainActivity the context
     * @param map a dictionary containing keys categories and values bitmap
     */
    public MainActivityAdapter(Context mainActivity, Map<String, Bitmap> map) {
        int count = 0;
        this.category = new String[map.size()];
        this.image = new Bitmap[map.size()];
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String key : keys) {
            this.category[count] = key;
            this.image[count] = map.get(key);
            count++;
        }
        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return category.length;
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
     * Object bean used to keep track of values related to the xml document
     */
    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    /**
     * Override of the getView method to inflate the xml file and instantiate it correctly
     * @param position  the position
     * @param convertView the current view
     * @param parent the parent view
     * @return the view once it is instantiated correctly
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView = convertView;
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.program_list, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
            holder.tv.setText(category[position]);
            holder.img.setImageBitmap(image[position]);
            rowView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
            holder.tv.setText(category[position]);
            holder.img.setImageBitmap(image[position]);
        }
        //Set the onclick to go to QAActivity
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, QAActivity.class);
                intent.putExtra("category", category[position]);
                intent.putExtra("imgBytes", imageToByte(image[position]));
                context.startActivity(intent);
            }
        });
        return rowView;
    }

    /**
     * Method to convert a bitmap to a byte array
     * @param bit the bitmap object converted to an array of bytes
     * @return the byte array
     */
    private byte[] imageToByte(Bitmap bit){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}