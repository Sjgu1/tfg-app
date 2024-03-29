package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
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
import sergiojuliogu.myapplication.Adapters.UsersAdapter;

public class ProjectActivity extends AppCompatActivity {

    private ProjectInfoTask mProjectTask = null;

    private JSONObject projectObject;
    private JSONArray usersObject;

    private JSONArray recalculadoUsersObject;

    private TextView projectNameView;
    private TextView projectDescriptionView;
    private TextView projectRepositoryView;
    private TextView projectStartDateView;
    private TextView projectEstimateEndView;
    private TextView projectEndDateView;
    private TextView projectRoleView;
    private ImageButton addUserView;

    private View mProgressView;
    private View mLoginFormView;

    private Context c;
    private String projectID;
    private int activityBrequestCode = 0;

    private GridView gridView;
    private boolean admin = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_charts:
                    Intent intent2 = new Intent(c, ChartProjectActivity.class);
                    startActivityForResult(intent2, activityBrequestCode);
                    return true;
                case R.id.navigation_edit:
                    if(admin){
                        Intent intent = new Intent(c, UpdateProjectActivity.class);
                        Bundle b = new Bundle();
                        b.putString("projectID", projectID.toString());
                        intent.putExtras(b); //Put your id to your next Intent
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, activityBrequestCode);
                    }else{
                        Toast.makeText(ProjectActivity.this, "Solo disponible para administradores y jefes." ,
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.navigation_sprints:
                    Intent intent = new Intent(c, SprintsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("projectID", projectID.toString());
                    intent.putExtras(b); //Put your id to your next Intent
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, activityBrequestCode);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        c = this.getApplicationContext();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("key");
            projectID = value;
            Session.setProjectSelected(value);
        }

        findViewsById();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        projectID = Session.getProjectSelected();
        showProgress(true);
        mProjectTask = new ProjectInfoTask(projectID);
        mProjectTask.execute((Void) null);
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
    private void findViewsById() {
        mLoginFormView = findViewById(R.id.project_form);
        mProgressView = findViewById(R.id.project_progress);

        projectNameView = (TextView) findViewById(R.id.label_project_name);
        projectDescriptionView = (TextView) findViewById(R.id.label_project_description);
        projectRepositoryView = (TextView) findViewById(R.id.label_project_repository);
        projectStartDateView = (TextView) findViewById(R.id.label_start_date);
        projectEstimateEndView = (TextView) findViewById(R.id.label_end_date);
        projectEndDateView = (TextView) findViewById(R.id.label_end);
        projectRoleView = (TextView) findViewById(R.id.label_project_role);
        addUserView = (ImageButton) findViewById(R.id.add_user_button);
        addUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, SearchUserActivity.class);
                Bundle b = new Bundle();
                b.putString("usersParticipantes", usersObject.toString()); //Your id
                b.putString("proyecto", projectObject.toString());
                intent.putExtras(b); //Put your id to your next Intent
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, activityBrequestCode);

            }
        });
        gridView = (GridView) findViewById(R.id.users_grid);

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
            projectID = projectID;

            Intent refresh = new Intent(c, ProjectActivity.class);
            finish(); //finish Activity.

            startActivity(refresh);//Start the same Activity

        }
    }

    /**
     * Represents an asynchronous task used to get project info information.
     */
    public class ProjectInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String project;
        private String projectObtenido;

        ProjectInfoTask(String idProject) {
            project = idProject;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/projects/"+project;
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
                        projectObject = obj;
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
            mProjectTask = null;

            showProgress(false);

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(ProjectActivity.this, "Error al obtener los datos del usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mProjectTask = null;
        }

        private void pintarDatos(){
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try{
                if(projectObject.has("name") ){
                    projectNameView.setText(projectObject.get("name").toString());
                }else{
                    projectNameView.setText("Nombre no obtenido");
                }

                if(projectObject.has("description") ){
                    projectDescriptionView.setText(projectObject.get("description").toString());
                }else{
                    projectDescriptionView.setText("");
                }

                if(projectObject.has("repository") ){
                    projectRepositoryView.setText(projectObject.get("repository").toString());
                }else{
                    projectRepositoryView.setText("Repositorio no obtenido.");
                }

                if(projectObject.has("start_date") ){
                    //Date date = dateFormat.parse(projectObject.get("start_date").toString().substring(0,10));
                    String dateStr = parseDate(projectObject.get("start_date").toString().substring(0,10));
                    projectStartDateView.setText(dateStr);
                }else{
                    projectStartDateView.setText("No establecida");
                }

                if(projectObject.has("estimated_end") ){
                    String dateStr = parseDate(projectObject.get("estimated_end").toString().substring(0,10));

                    projectEstimateEndView.setText(dateStr);
                }else{
                    projectEstimateEndView.setText("No establecida");
                }

                if(projectObject.has("end_date") ){
                    String dateStr = parseDate(projectObject.get("end_date").toString().substring(0,10));
                    projectEndDateView.setText(dateStr);
                }else{
                    projectEndDateView.setText("No finalizado");
                }

                if(projectObject.get("users").toString().equals("[]") ){
                    Toast.makeText(ProjectActivity.this, "No existen usuarios." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    usersObject = (JSONArray)projectObject.get("users");
                    JSONObject  userArrayObject= new JSONObject();
                    JSONObject  userObject= new JSONObject();
                    JSONObject  roleObject= new JSONObject();

                    String conectado = Session.getUsername();
                    for ( int i = 0; i< usersObject.length(); i++){
                        userArrayObject = (JSONObject) usersObject.get(i);
                        userObject =  userArrayObject.getJSONObject("user");
                        roleObject = userArrayObject.getJSONObject("role");
                        if(userObject.get("username").toString().equals(conectado)){
                            Session.setIdUsername(userObject.getString("_id"));
                            projectRoleView.setText(roleObject.get("name").toString());
                            Session.setRolSelected(roleObject.get("name").toString());
                            if(roleObject.get("name").toString().equals("Admin")){
                                admin = true;
                            }
                        }
                    }

                    UsersAdapter usersAdapter = new UsersAdapter(c, usersObject);
                    gridView.setAdapter(usersAdapter);
                    // Setting on Touch Listener for handling the touch inside ScrollView

                    gridView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }

                    });

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
