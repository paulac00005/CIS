package com.example.paulac.cis;

/**
 * Created by paulac on 3/14/16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class StoriesAdapter extends ArrayAdapter<StoriesModel> {
    ArrayList<StoriesModel> storiesList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public StoriesAdapter(Context context, int resource, ArrayList<StoriesModel> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        storiesList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.CustomIV = (ImageView) v.findViewById(R.id.simage);
            holder.sTitle = (TextView) v.findViewById(R.id.stitle);
            holder.sDate = (TextView) v.findViewById(R.id.sdate);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.CustomIV.setImageResource(R.drawable.imagehere);
        new DownloadImageTask(holder.CustomIV).execute(storiesList.get(position).getImage_path());
        holder.sTitle.setText(storiesList.get(position).getTitle());
        holder.sDate.setText(storiesList.get(position).getImage_path());
        return v;

    }

    static class ViewHolder {
        public ImageView CustomIV;
        public TextView sTitle;
        public TextView sDate;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    }