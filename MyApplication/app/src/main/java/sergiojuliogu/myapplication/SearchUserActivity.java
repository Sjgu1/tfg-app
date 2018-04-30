package sergiojuliogu.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
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


public class SearchUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    private GridView list;
    private SearchUserAdapter adapter;
    private SearchView editsearch;


    private JSONArray users = new JSONArray();
    private JSONArray roles = new JSONArray();

    private GetRolesTask mGetRolesTask = null;
    private GetUsersTask mGetUsersTask = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mGetRolesTask = new GetRolesTask();
        mGetRolesTask.execute((Void) null);

        mGetUsersTask = new GetUsersTask();
        mGetUsersTask.execute((Void) null);


        // Pass results to ListViewAdapter Class
        adapter = new SearchUserAdapter(this, users);

        list = (GridView) findViewById(R.id.grid_search_users);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.simpleSearchView);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }


    /**
     * Represents an asynchronous task used to get roles.
     */
    public class GetRolesTask extends AsyncTask<Void, Void, Boolean> {

        GetRolesTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = "https://sergiojuliogu-tfg-2018.herokuapp.com/users/" + Session.getUsername()+"/roles";
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
                Log.i("Resultado", status);
                if(status.equals("200")) {
                    try {

                        roles = new JSONArray(result);
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
            mGetRolesTask = null;

            if (success) {
                Log.i("Proyectos", success.toString());
            } else {
                Toast.makeText(SearchUserActivity.this, "Error al obtener los usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetRolesTask = null;
        }
    }

    /**
     * Represents an asynchronous task used to get roles.
     */
    public class GetUsersTask extends AsyncTask<Void, Void, Boolean> {

        GetUsersTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = "https://sergiojuliogu-tfg-2018.herokuapp.com/users";
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
                Log.i("Resultado", status);
                if(status.equals("200")) {
                    try {

                        users = new JSONArray(result);
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
            mGetUsersTask = null;

            if (success) {
                Log.i("Proyectos", success.toString());
            } else {
                Toast.makeText(SearchUserActivity.this, "Error al obtener los usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGetUsersTask = null;
        }
    }
}