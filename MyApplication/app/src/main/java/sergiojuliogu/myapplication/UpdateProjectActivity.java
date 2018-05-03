package sergiojuliogu.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateProjectActivity extends AppCompatActivity {

    private ProjectInfoTask mProjectTask = null;

    private Context c;
    private JSONObject projectObject;
    private JSONArray usersObject;
    private JSONArray rolesObject;
    private String projectID;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;


    private ImageButton startDate;
    private ImageButton endDate;
    private EditText startDateInput;
    private EditText endDateInput;
    private TextView endateText;
    private EditText nomInput;
    private EditText descrInput;
    private Switch mSwitch;
    private EditText repoInput;
    private Button updateProjButton;
    private ListView userListView;
    private View mProgressView;


    private GetRolesTask mGetRolesTask = null;
    private ProjectInfoTask mGeProjectTask = null;
    private UpdateUserProjectTask mUpdateUserTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_project);

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        c = this.getApplicationContext();
        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("projectID");
            projectID = value;
            Session.setProjectSelected(value);
        }
        mGetRolesTask = new GetRolesTask();
        mGetRolesTask.execute((Void) null);


        findViewsById();
        setDateTimeField();

        mGeProjectTask = new ProjectInfoTask(projectID);
        mGeProjectTask.execute((Void) null);


    }

    private void findViewsById() {
        startDateInput = (EditText) findViewById(R.id.etxt_fromdate_update);
        startDateInput.setInputType(InputType.TYPE_NULL);
        startDateInput.requestFocus();

        endDateInput = (EditText) findViewById(R.id.etxt_todate_update);
        endDateInput.setInputType(InputType.TYPE_NULL);

        startDate = (ImageButton) findViewById(R.id.imageButtonUpdate);
        endDate = (ImageButton) findViewById(R.id.imageButton2Update);

        nomInput = (EditText) findViewById(R.id.input_nombre_update);
        descrInput = (EditText) findViewById(R.id.input_description_update);
        repoInput = (EditText) findViewById(R.id.input_respository_update);
        endateText = (TextView) findViewById(R.id.text_end_update);

        mSwitch = (Switch) findViewById(R.id.switch_update);
        mSwitch.setChecked(false);
        userListView = (ListView) findViewById(R.id.list_view_update);

        updateProjButton = (Button) findViewById(R.id.button_update_project);
        updateProjButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdateInfoProject();
            }
        });

    }

    private void attemptUpdateInfoProject(){

        if (mUpdateUserTask != null) {
            return;
        }

        // Reset errors.
        nomInput.setError(null);
        descrInput.setError(null);
        repoInput.setError(null);

        startDateInput.setError(null);
        endDateInput.setError(null);


        // Store values at the time of the register attempt.
        String nameProject = nomInput.getText().toString();
        String descriptionProject = descrInput.getText().toString();
        String repositoryProject = repoInput.getText().toString();
        String startProject = startDateInput.getText().toString();
        String endProject = endDateInput.getText().toString();
        boolean finalizado = mSwitch.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name, if the user entered one.
        if (TextUtils.isEmpty(nameProject)) {
            nomInput.setError(getString(R.string.error_field_required));
            focusView = nomInput;
            cancel = true;
        }

        try{
            // Check for a valid email address.
            if (!startProject.equals("Inicio")) {
                if (!endProject.equals("Fin")) {
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    Date dateStart = format.parse(startProject);
                    Date dateEnd = format.parse(endProject);

                    if(dateEnd.before(dateStart)){
                        endDateInput.setError(getString(R.string.error_invalid_date));
                        focusView = endDateInput;
                        cancel = true;
                    }
                }
            }
        }catch (Exception e){
            Log.e("Error de fechas", e.toString());
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            //showProgress(true);
            mUpdateUserTask = new UpdateUserProjectTask(nameProject , repositoryProject, descriptionProject , startProject, endProject, finalizado);
            mUpdateUserTask.execute((Void) null);
        }

    }
    private void setDateTimeField() {
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();

            }
        });
        startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        endDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDateInput.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDateInput.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Represents an asynchronous task used to get project info.
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

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(c, "Error al obtener los datos del usuario." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProjectTask = null;
        }

        private void pintarDatos(){
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try{
                if(projectObject.has("name") ){
                    nomInput.setText(projectObject.get("name").toString());
                }else{
                    nomInput.setText("");
                }

                if(projectObject.has("description") ){
                    descrInput.setText(projectObject.get("description").toString());
                }else{
                    descrInput.setText("");
                }

                if(projectObject.has("repository") ){
                    repoInput.setText(projectObject.get("repository").toString());
                }else{
                    repoInput.setText("");
                }

                if(projectObject.has("start_date") ){
                    //Date date = dateFormat.parse(projectObject.get("start_date").toString().substring(0,10));
                    String dateStr = parseDate(projectObject.get("start_date").toString().substring(0,10));
                    startDateInput.setText(dateStr);
                }else{
                    startDateInput.setText("Inicio");
                }

                if(projectObject.has("estimated_end") ){
                    String dateStr = parseDate(projectObject.get("estimated_end").toString().substring(0,10));

                    endDateInput.setText(dateStr);
                }else{
                    endDateInput.setText("Fin");
                }

                if(projectObject.has("end_date") ){
                    String dateStr = parseDate(projectObject.get("end_date").toString().substring(0,10));
                    endateText.setText(dateStr);
                }else{
                    endateText.setText("No finalizado");
                }

                if(projectObject.get("users").toString().equals("[]") ){
                    Toast.makeText(c, "No existen usuarios." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    usersObject = (JSONArray)projectObject.get("users");

                    UsersUpdateAdapter usersAdapter = new UsersUpdateAdapter(c, usersObject, rolesObject);
                    userListView.setAdapter(usersAdapter);
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


    /**
     * Represents an asynchronous task used to get roles.
     */
    public class GetRolesTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray rolesObtenidos;

        GetRolesTask() { }


        @Override
        protected Boolean doInBackground(Void... params) {
            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/roles";
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

                        rolesObject = new JSONArray(result);
                        rolesObtenidos = new JSONArray(result);
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
                //Toast.makeText(c, "He conseguido los roles." ,
                 //       Toast.LENGTH_SHORT).show();
            }else{
               // Toast.makeText(c, "No he conseguido los roles." ,
                //        Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
                mGetRolesTask = null;
        }
    }

    /**
     * Represents an asynchronous task used to update a project.
     */
    public class UpdateUserProjectTask extends AsyncTask<Void, Void, Boolean> {
        private String idProject = null;
        private String error = "";
        private String name = null;
        private String description = null;
        private String repository = null;
        private String start_date = "vacio";
        private String end_date = "vacio";
        private String estimated_end = "vacio";
        private String username = null;

        UpdateUserProjectTask( String name, String repository, String description, String startDate, String estimatedEnd, boolean finalizado) {

                this.idProject = Session.getProjectSelected();
                this.name = name;
                this.repository = repository;
                this.description = description;
                this.start_date = startDate;
                this.estimated_end = estimatedEnd;
                if(finalizado){
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = new Date();
                    this.end_date = dateFormat.format(date);
                }else{
                    this.end_date = "vacio";
                }
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected();
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{
                if(name != null){
                    body.put("name", name);
                }
                if(description != null){
                    body.put("description", description);
                }
                if(repository != null){
                    body.put("repository", repository);
                }

                if(!start_date.equals("vacio")){
                    body.put("start_date", start_date);
                }
                if(!estimated_end.equals("vacio")){
                    body.put("estimated_end", estimated_end);
                }
                if(!end_date.equals("vacio")){
                    body.put("end_date", end_date);
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

                connection.connect();
                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
                String status = connection.getResponseCode() + "";

                if(status.equals("400")){
                    if(result.equals("El repositorio tiene que ser una url valida")){
                        error = "repositorio";
                        return false;
                    }else{
                        error = "otros";
                        return false;
                    }
                }
                if(status.equals("404")){
                    error = "otros";
                    return false;
                }
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
            mUpdateUserTask = null;
            if (success) {
                setResult(RESULT_OK);
                finish();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateUserTask = null;
        }
        public String parseDate(String time) {



            String inputPattern = "yyyy-dd-MM";

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

        private void mostrarErroresRespuesta(){

            if(error.equals("repositorio")){
                repoInput.setError("URL no válida. Ejemplo: www.ejemplo.com/...");
                repoInput.requestFocus();

            }else{
                Toast.makeText(c, "Ha ocurrido agún problema." ,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

}
