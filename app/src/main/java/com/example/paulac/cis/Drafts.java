package com.example.paulac.cis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Drafts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    EditText etTitle, etContent, etAuthor;
    Button postbtn, cam;
    ImageView uploadedfile;


    Button btpic, btnup;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1;

    final int REQUEST_CODE = 1;

    Bitmap bitmap;
    private int requestCode;
    private int resultCode;
    private Intent data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        etAuthor = (EditText) findViewById(R.id.etAuthor);

        postbtn = (Button) findViewById(R.id.postBtn);
        postbtn.setOnClickListener(this);

        cam = (Button) findViewById(R.id.cam);

        cam.setOnClickListener(this);

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


        /*if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            uploadedfile.setImageURI(selectedImage);
        }
    }*/

    private class PostTask extends AsyncTask<String, Void, Void> {

        Boolean result = false;
        Bitmap image;

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
                try {
                    ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
                    postParams.add(new BasicNameValuePair("title", title));
                    postParams.add(new BasicNameValuePair("content", content));
                    postParams.add(new BasicNameValuePair("author", author));
                    postParams.add(new BasicNameValuePair("key", key));
                   // postParams.add(new BasicNameValuePair("uploadedfile", uploadedfile));


                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(postParams));
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
            PostTask loginTask = new PostTask();
            loginTask.execute("http://10.4.101.44/sbs/post_story.php");
            }
        else if(v.getId()==R.id.uploadedfile) {
            Intent galleryIntent = new Intent(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
            startActivityForResult(galleryIntent, REQUEST_CODE);
        }
        else if(v.getId()==R.id.cam){

        }
        }
    }





















/*import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.ArrayList;
        import java.util.Formatter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername, etPassword;
    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        login = (Button)findViewById(R.id.login);

        login.setOnClickListener(this);

    }



    @Override
    public void onClick(View v){
        if(v.getId()==R.id.login) {
            LoginTask loginTask = new LoginTask();
            loginTask.execute("http://10.4.101.44/sbs/login.php");
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sending Data...");
            dialog.show();
        }

        Boolean result = false;

        InputStream is1;
        String text = "";
        private String error = "";

        String username = etUsername.getText().toString();
        String rawpass = etPassword.getText().toString();
        String key = "70930f27";

        String password = MD5(sha1(rawpass) + MD5(key));


        @Override
        protected Void doInBackground(String... urls) {
            for(String url1 : urls){



                try {
                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("username", username));
                    params.add(new BasicNameValuePair("password", password));
                    params.add(new BasicNameValuePair("key", key));
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(params));
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
                    reader = new BufferedReader(new InputStreamReader(is1 ,"iso-8859-1"), 8);
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
        protected void onPostExecute(Void arg0) {
            if(dialog.isShowing()){
            dialog.dismiss();
            }

            // Get username, password from EditText
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

           /* // Check if username, password is filled
            if(username.trim().length() > 0 && password.trim().length() > 0){
                // For testing puspose username, password is checked with sample data
                // username = test
                // password = test
                if(username.equals("test") && password.equals("test")){

                    // Creating user login session
                    // For testing i am stroing name, email as follow
                    // Use user real data
                    userLocalStore.createLoginSession("Android Hive", "anroidhive@gmail.com");

                    // Staring MainActivity
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();

                }else{
                    // username / password doesn't match
                    alert.showAlertDialog(MainActivity.this, "Login failed..", "Username/Password is incorrect", false);
                }
            }else{
                // user didn't entered username or password
                // Show alert asking him to enter the details
                alert.showAlertDialog(MainActivity.this, "Login failed..", "Please enter username and password", false);
            }
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, Drafts.class));

        }
    }
            /*if(text.equals("Login Failed")){
                Intent in = new Intent(MainActivity.this, MainActivity.class);
                startActivity(in);
            }
            else{
                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
            }


    private String sha1(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }
    private String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private final String MD5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}*/
