package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import sergiojuliogu.myapplication.Adapters.ProjectsAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class AvatarsActivity extends AppCompatActivity {

    private JSONObject userObject;
    private Context c;
    private UserInfoTask mUserTask = null;


    private UserUpdateAvatarTask mUserInfo = null;
    private View mProgressView;
    private View mAvatarsFormView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatars);

        c = this.getApplicationContext();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new AvatarsActivity.ImageAdapter(this));

        mProgressView = findViewById(R.id.avatars_progress);
        mAvatarsFormView = findViewById(R.id.avatars_form);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                showProgress(true);

                showProgress(true);
                mUserInfo = new UserUpdateAvatarTask(position);
                mUserInfo.execute((Void) null);
            }
        });

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null)
            value = b.getString("user");

        try {

            JSONObject obj = new JSONObject(value);
            userObject = obj;
            System.out.println("el user");
            System.out.println(userObject.toString());


        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + value + "\"");
        }

        showProgress(true);
        mUserTask = new UserInfoTask();
        mUserTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAvatarsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAvatarsFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAvatarsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAvatarsFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.avatar_001, R.drawable.avatar_002,
                R.drawable.avatar_003, R.drawable.avatar_004,
                R.drawable.avatar_005, R.drawable.avatar_006,
                R.drawable.avatar_007, R.drawable.avatar_008,
                R.drawable.avatar_009, R.drawable.avatar_010,
                R.drawable.avatar_011, R.drawable.avatar_012,
                R.drawable.avatar_013, R.drawable.avatar_014,
                R.drawable.avatar_015, R.drawable.avatar_016,
                R.drawable.avatar_017, R.drawable.avatar_018,
                R.drawable.avatar_019, R.drawable.avatar_020,
                R.drawable.avatar_021, R.drawable.avatar_022,
                R.drawable.avatar_023, R.drawable.avatar_024,
                R.drawable.avatar_025, R.drawable.avatar_026,
                R.drawable.avatar_027, R.drawable.avatar_028,
                R.drawable.avatar_029, R.drawable.avatar_030,
                R.drawable.avatar_031, R.drawable.avatar_032,
                R.drawable.avatar_033, R.drawable.avatar_034,
                R.drawable.avatar_035, R.drawable.avatar_036,
                R.drawable.avatar_037, R.drawable.avatar_038,
                R.drawable.avatar_039, R.drawable.avatar_040,
                R.drawable.avatar_041, R.drawable.avatar_042,
                R.drawable.avatar_043, R.drawable.avatar_044,
                R.drawable.avatar_045, R.drawable.avatar_046,
                R.drawable.avatar_047, R.drawable.avatar_048,
                R.drawable.avatar_049, R.drawable.avatar_050


        };

    }

    /**
     * Represents an asynchronous for update user avatar
     */
    public class UserUpdateAvatarTask extends AsyncTask<Void, Void, Boolean> {

        int position;
        UserUpdateAvatarTask(int pos) {
            position = pos;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{
                Log.i("user", userObject.toString());
                if(userObject.has("avatar") ){
                    body.put("avatar", userObject.get("avatar").toString());
                }
                position  = position + 1;
                String avatar = "";
                Log.i("pos", String.valueOf(position));

                if(position < 10){
                    avatar  = "avatar_00"+String.valueOf(position);
                }else{
                    avatar  = "avatar_0"+String.valueOf(position);
                }
                body.put("avatar", avatar);

                if(userObject.has("username") ){
                    body.put("username", userObject.get("username").toString());
                }
                if(userObject.has("password") ){
                    body.put("password", userObject.get("password").toString());
                }
                if(userObject.has("name") ){
                    body.put("name", userObject.get("name").toString());
                }
                if(userObject.has("email") ){
                    body.put("email", userObject.get("email").toString());
                }
                if(userObject.has("surname") ){
                    body.put("surname", userObject.get("surname").toString());
                }


                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Authorization", Session.getToken());

                connection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(body.toString());
                writer.close();

                connection.connect();

                String status = connection.getResponseCode() + "";

                Thread.sleep(2000);
                return true;
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mUserInfo = null;
            showProgress(false);

            if (success) {
                setResult(300);

                finish();
            } else {
                Toast.makeText(AvatarsActivity.this, "Error al actualizar",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mUserInfo = null;
        }
    }

    /**
     * Represents an asynchronous task used to get users information.
     */
    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String user;


        UserInfoTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            try{


                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", Session.getToken());
                connection.connect();


                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                // Response: 400
                // Log.e("Response", connection.getResponseMessage() + "");
                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String status = connection.getResponseCode() + "";
                result = sb.toString();
                if(status.equals("200")) {
                    try {

                        JSONObject obj = new JSONObject(result);
                        userObject = obj;
                        return true;

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON good: \"" + result + "\"");
                    }
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mUserTask = null;
            showProgress(false);
            if (success) {
            } else {
                Toast.makeText(AvatarsActivity.this, "Error al obtener los datos del usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(true);

            mUserTask = null;
        }

    }
}