package com.example.paulac.cis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Drafts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    EditText etTitle, etContent, etAuthor;
    Button postbtn, cam, gallery;
    ImageView uploadedfile;

    final int PIC_GALLERY_CODE = 1;
    final int PIC_CAMERA_CODE = 2;
    Bitmap bitmap;
    File file;
    Uri file_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        etAuthor = (EditText) findViewById(R.id.etAuthor);

        //postbtn = (Button) findViewById(R.id.postBtn);
        //postbtn.setOnClickListener(this);

        //cam = (Button) findViewById(R.id.cam);
       // gallery = (Button)findViewById(R.id.gallery);

//        cam.setOnClickListener(this);
    //    gallery.setOnClickListener(this);

        uploadedfile = (ImageView) findViewById(R.id.uploadedfile);
        uploadedfile.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case PIC_GALLERY_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
            case PIC_CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = file_uri;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if(selectedImageUri != null){
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                if (filePath != null) {
                    decodeFile(filePath);
                } else {
                    bitmap = null;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }


    private class PostTask extends AsyncTask<String, Void, Void> {

        Boolean result = false;

        InputStream is1;
        String text = "";
        private String error = "";

        String title = etTitle.toString();
        String content = etContent.toString();
        String author = etAuthor.toString();
        String key = "70930f27";

        ProgressDialog dialog = new ProgressDialog(Drafts.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Sending data . . .");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            for (String url1 : urls) {


                BitmapFactory.Options bfo;
                ByteArrayOutputStream bao ;

                bfo = new BitmapFactory.Options();
                bfo.inSampleSize = 2;

                bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte [] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                ArrayList nameValuePairs = new ArrayList();
                nameValuePairs.add(new BasicNameValuePair("title", title));
                nameValuePairs.add(new BasicNameValuePair("content", content));
                nameValuePairs.add(new BasicNameValuePair("author", author));
                nameValuePairs.add(new BasicNameValuePair("key", key));
                nameValuePairs.add(new BasicNameValuePair("uploadedfile",ba1));
                Log.v("log_tag", System.currentTimeMillis()+".jpg");

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = client.execute(post);
                    is1 = response.getEntity().getContent();

                    result = true;

                } catch (ClientProtocolException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                } catch (IOException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                }

                BufferedReader reader;

                try {
                    reader = new BufferedReader(new InputStreamReader(is1, "iso-8859-1"), 8);
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        text += line + "\n";
                    }
                } catch (UnsupportedEncodingException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                } catch (IOException e) {
                    error += "\nClientProtocolException: " + e.getMessage();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(Drafts.this, text, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drafts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.create) {
            startActivity(new Intent(Drafts.this, Drafts.class));
        } else if (id == R.id.stories) {
            startActivity(new Intent(Drafts.this, Stories.class));
        } else if (id == R.id.settings) {

        } else if (id == R.id.logout) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.postBtn){
            PostTask postTask = new PostTask();
            postTask.execute("http://10.4.101.44/sbs/post_story.php");
            }
        else if(v.getId()==R.id.uploadedfile) {

        }/*else if(v.getId()==R.id.cam){

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getFileUri();
            i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
            startActivityForResult(i, 10);

        }*/else if(v.getId()==R.id.gallery){

            try {
                Intent gintent = new Intent();
                gintent.setType("image/*");
                gintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(gintent, "Select Picture"),
                        PIC_GALLERY_CODE);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }}}

    public void getFileUri(){
        String image_name = "image";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
        file_uri = Uri.fromFile(file);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        uploadedfile.setImageBitmap(bitmap);

    }

}