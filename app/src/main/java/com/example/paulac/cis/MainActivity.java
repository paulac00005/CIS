package com.example.paulac.cis;

//PASSWORD --->>>> md5(sha1(password) + md5(key))

import android.app.ProgressDialog;
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
            if(dialog.isShowing()){                dialog.dismiss();
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
            }*/
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
            }*/


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

    }
