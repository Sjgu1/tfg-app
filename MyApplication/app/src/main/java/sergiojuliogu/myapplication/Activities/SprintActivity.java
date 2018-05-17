package sergiojuliogu.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;
import sergiojuliogu.myapplication.Adapters.StatusAdapter;

public class SprintActivity extends AppCompatActivity {

    private SprintInfoTask mSprintTask = null;

    private JSONObject sprintObject;
    private JSONArray statusObject;

    private TextView projectNameView;
    private TextView projectDescriptionView;
    private TextView projectStartDateView;
    private TextView projectEstimateEndView;
    private TextView projectEndDateView;
    private TextView projectRoleView;
    private Context c;
    private String sprintID;
    private int activityBrequestCode = 0;

    private ListView gridView;
    private boolean admin = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_charts_sprint:
                    return true;
                case R.id.navigation_edit_sprint:
                    Intent intent = new Intent(c, SprintUpdateActivity.class);
                    Bundle b = new Bundle();
                    b.putString("sprintID", sprintID.toString());
                    intent.putExtras(b); //Put your id to your next Intent
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, activityBrequestCode);
                    return true;
                case R.id.navigation_sprints_sprint:
                    Intent intent2 = new Intent(c, StatusActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent2, activityBrequestCode);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        c = this.getApplicationContext();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationSprint);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("key");
            sprintID = value;
            Session.setSprintSelected(value);
        }

        findViewsById();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        sprintID = Session.getSprintSelected();
        mSprintTask = new SprintInfoTask(sprintID);
        mSprintTask.execute((Void) null);
    }

    private void findViewsById() {
        projectNameView = (TextView) findViewById(R.id.nameSprint);
        projectDescriptionView = (TextView) findViewById(R.id.descriptionSprint);

        projectStartDateView = (TextView) findViewById(R.id.sprint_inicio);
        projectEstimateEndView = (TextView) findViewById(R.id.sprint_estimated_end);
        projectEndDateView = (TextView) findViewById(R.id.sprint_end_date);
        gridView = (ListView) findViewById(R.id.sprint_status_list_update);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 300){
            activityBrequestCode =0;
            setResult(RESULT_OK);
            finish(); //finish Activity.
        }
        if (requestCode == activityBrequestCode && resultCode == RESULT_OK){
            activityBrequestCode =0;
            Intent refresh = new Intent(c, SprintActivity.class);
            finish(); //finish Activity.
            startActivity(refresh);//Start the same Activity
        }
    }

    /**
     * Represents an asynchronous task used to get sprint info information.
     */
    public class SprintInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String sprint;
        private String sprintObtenido;

        SprintInfoTask(String idSprint) {
            sprint = idSprint;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/projects/"+ Session.getProjectSelected()+"/sprints/"+sprint;
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
                if(status.equals("200")) {
                    try {

                        JSONObject obj = new JSONObject(result);
                        sprintObject = obj;
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
            mSprintTask = null;
            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(SprintActivity.this, "Error al obtener los datos del sprint." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSprintTask = null;
        }

        private void pintarDatos(){
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try{
                if(sprintObject.has("name") ){
                    projectNameView.setText(sprintObject.get("name").toString());
                }else{
                    projectNameView.setText("Nombre no obtenido");
                }

                if(sprintObject.has("description") ){
                    projectDescriptionView.setText(sprintObject.get("description").toString());
                }else{
                    projectDescriptionView.setText("");
                }

                if(sprintObject.has("start_date") ){
                    //Date date = dateFormat.parse(projectObject.get("start_date").toString().substring(0,10));
                    String dateStr = parseDate(sprintObject.get("start_date").toString().substring(0,10));
                    projectStartDateView.setText(dateStr);
                }else{
                    projectStartDateView.setText("No establecida");
                }

                if(sprintObject.has("estimated_end") ){
                    String dateStr = parseDate(sprintObject.get("estimated_end").toString().substring(0,10));

                    projectEstimateEndView.setText(dateStr);
                }else{
                    projectEstimateEndView.setText("No establecida");
                }

                if(sprintObject.has("end_date") ){
                    String dateStr = parseDate(sprintObject.get("end_date").toString().substring(0,10));
                    projectEndDateView.setText(dateStr);
                }else{
                    projectEndDateView.setText("No finalizado");
                }

                if(sprintObject.get("status").toString().equals("[]") ){
                    Toast.makeText(SprintActivity.this, "No existen estados." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    statusObject = (JSONArray) sprintObject.get("status");

                    StatusAdapter statusAdapter = new StatusAdapter(c, statusObject);
                    gridView.setAdapter(statusAdapter);

                }

            }catch (JSONException e){
                Log.e("JSON", e.toString());
            }catch (Exception e){
                Log.e("Error", e.toString());

            }

        }
        public String parseDate(String time) {

            String inputPattern = "yyyy-MM-dd";
            String outputPattern = "MM/dd/yyyy";

            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }
    }
}
