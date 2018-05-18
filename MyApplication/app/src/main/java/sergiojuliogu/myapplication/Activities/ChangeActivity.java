package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sergiojuliogu.myapplication.Adapters.ChangesAdapter;
import sergiojuliogu.myapplication.Adapters.UserTaskAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class ChangeActivity extends AppCompatActivity {


    private Context c;
    private ListView listView;
    private JSONObject taskObject;
    private JSONArray changesArray;
    private View mProgressView;
    private View mLoginFormView;


    private InfoTask mInfoTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        c = getApplicationContext();

        if(!Session.getLoged()){
            finish();
        }

        findViewsById();
        showProgress(true);
        mInfoTask = new InfoTask(c);
        mInfoTask.execute((Void) null);
    }

    private void findViewsById(){
        mProgressView = findViewById(R.id.change_progress);
        mLoginFormView = findViewById(R.id.change_form);
        listView = (ListView) findViewById(R.id.changes_listview);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * Represents an asynchronous task used to get task info.
     */
    public class InfoTask extends AsyncTask<Void, Void, Boolean> {
        Context c;
        InfoTask(Context c) {
            this.c=c;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+
                    "/projects/"+Session.getProjectSelected()+
                    "/sprints/"+Session.getSprintSelected()+
                    "/status/"+Session.getStatusSelected()+
                    "/tasks/"+Session.getTaskSelected();
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
                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String status = connection.getResponseCode() + "";
                result = sb.toString();
                //Log.i("estado", result);

                if(status.equals("200")) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        taskObject = obj;

                        changesArray = obj.getJSONArray("changes");
                        Log.i("Salida", changesArray.toString());
                        return true;
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                        return false;
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
            mInfoTask = null;
            showProgress(false);

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(ChangeActivity.this, "Error al obtener los datos de la tarea." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);

            mInfoTask = null;
        }

        private void pintarDatos(){
            if(changesArray.length() <= 0){
                Toast.makeText(ChangeActivity.this, "No hay notificaciones." ,
                        Toast.LENGTH_SHORT).show();
            }else{
                ChangesAdapter changesAdapter = new ChangesAdapter(c, changesArray);
                listView.setAdapter(changesAdapter);
            }
        }
    }

}
