package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;
import sergiojuliogu.myapplication.Adapters.StatusAdapter;

public class SprintUpdateActivity extends AppCompatActivity {

    private SprintInfoTask mGetSprintTask = null;
    private UpdateSprintProjectTask mUpdateSprintTask = null;
    private SprintDeleteTask mSprintDeleteTask = null;
    private NewStatusTask mNewStatusTask = null;

    private Context c;
    private JSONObject sprintObject;
    private JSONArray statusObject;
    private String sprintID;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    private ImageButton startDate;
    private ImageButton endDate;

    //UI References
    private EditText startDateInput;
    private EditText endDateInput;
    private Switch mSwitch;
    private EditText nomInput;
    private EditText descrInput;
    private TextView eliminarButton;
    private TextView endateText;
    private Button updateSprintButton;
    private ListView statusListView;
    private TextView addStatus;
    private String m_Text = "";


    private View mProgressView;
    private View mLoginFormView;
    private View mBotonesActualizarSprint;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_update);

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        c = this.getApplicationContext();
        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("sprintID");
            sprintID = value;
            Session.setSprintSelected(sprintID);
        }

        findViewsById();
        setDateTimeField();

        showProgress(true);
        mGetSprintTask = new SprintInfoTask(sprintID);
        mGetSprintTask.execute((Void) null);
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

            mBotonesActualizarSprint.setVisibility(show ? View.GONE : View.VISIBLE);
            mBotonesActualizarSprint.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBotonesActualizarSprint.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mBotonesActualizarSprint.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void findViewsById() {
        mLoginFormView = findViewById(R.id.sprint_update_form);
        mProgressView = findViewById(R.id.sprint_update_progress);
        mBotonesActualizarSprint = findViewById(R.id.botones_actualizar_sprint);

        startDateInput = (EditText) findViewById(R.id.etxt_fromdate_sprint_update);
        startDateInput.setInputType(InputType.TYPE_NULL);
        startDateInput.requestFocus();

        endDateInput = (EditText) findViewById(R.id.etxt_todate_sprint_update);
        endDateInput.setInputType(InputType.TYPE_NULL);

        startDate = (ImageButton) findViewById(R.id.imageButton_sprint_update);
        endDate = (ImageButton) findViewById(R.id.imageButton2_sprint_update);

        nomInput = (EditText) findViewById(R.id.input_nombre_sprint_update);
        descrInput = (EditText) findViewById(R.id.input_description_sprint_update);
        endateText = (TextView) findViewById(R.id.text_end_update_finalizado);

        mSwitch = (Switch) findViewById(R.id.switch_update_sprint);
        mSwitch.setChecked(false);
        statusListView = (ListView) findViewById(R.id.sprint_status_list_update);
        statusListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });

        updateSprintButton = (Button) findViewById(R.id.update_sprint_button);
        updateSprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdateInfoSprint();
            }
        });

        eliminarButton = (TextView) findViewById(R.id.delete_sprint);
        eliminarButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attemptDeleteSprint(v);
            }
        });

        addStatus = (TextView) findViewById(R.id.new_status_button);
        addStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SprintUpdateActivity.this);
                builder.setTitle("Nombre del nuevo estado");

                final EditText input = new EditText(c);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        if (!TextUtils.isEmpty(m_Text)){
                            showProgress(true);

                            mNewStatusTask = new NewStatusTask(m_Text);
                            mNewStatusTask.execute((Void) null);
                        }else{
                            Toast.makeText(SprintUpdateActivity.this, "Se necesita un nombre." ,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
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

    private void updateActivity(){
        setResult(RESULT_OK);
        finish();
    }
    private void  attemptDeleteSprint(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        showProgress(true);

                        mSprintDeleteTask = new SprintDeleteTask(Session.getSprintSelected());
                        mSprintDeleteTask.execute((Void) null);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("¿Estás seguro que quieres eliminar el sprint?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    private void attemptUpdateInfoSprint(){

        if (mUpdateSprintTask != null) {
            return;
        }

        // Reset errors.
        nomInput.setError(null);
        descrInput.setError(null);

        startDateInput.setError(null);
        endDateInput.setError(null);


        // Store values at the time of the register attempt.
        String nameSprint = nomInput.getText().toString();
        String descriptionSprint = descrInput.getText().toString();
        String startSprint = startDateInput.getText().toString();
        String endSprint = endDateInput.getText().toString();
        boolean finalizado = mSwitch.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name, if the user entered one.
        if (TextUtils.isEmpty(nameSprint)) {
            nomInput.setError(getString(R.string.error_field_required));
            focusView = nomInput;
            cancel = true;
        }

        try{
            // Check for a valid email address.
            if (!startSprint.equals("Inicio")) {
                if (!endSprint.equals("Fin")) {
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    Date dateStart = format.parse(startSprint);
                    Date dateEnd = format.parse(endSprint);

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

            showProgress(true);
            mUpdateSprintTask = new UpdateSprintProjectTask(nameSprint , descriptionSprint , startSprint, endSprint, finalizado);
            mUpdateSprintTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous task used to delete an sprint .
     */
    public class SprintDeleteTask extends AsyncTask<Void, Void, Boolean> {
        private String sprint;
        private String projectObtenido;

        SprintDeleteTask(String idSprint) {
            sprint = idSprint;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+ Session.getProjectSelected() +"/sprints/" + sprint;
            //String to place our result in
            String result;

            try{

                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Authorization", Session.getToken());

                connection.connect();

                connection.connect();
                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                    Thread.sleep(2000);
                    return true;
                } else {
                    Thread.sleep(2000);
                    return false;
                }
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mSprintDeleteTask = null;
            showProgress(false);


            if (success) {
                Session.setSprintSelected("");
                setResult(300);
                finish();
            } else {
                Toast.makeText(SprintUpdateActivity.this, "No se ha podido eliminar el sprint." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mSprintDeleteTask = null;
        }


    }

    /**
     * Represents an asynchronous task used to get sprint info.
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
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/projects/"+Session.getProjectSelected()+"/sprints/"+sprint;
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
            mGetSprintTask = null;
            showProgress(false);

            if (success) {
                pintarDatos();
            } else {
                Toast.makeText(c, "Error al obtener los datos del sprint." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

            showProgress(false);

            mGetSprintTask = null;
        }

        private void pintarDatos(){
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try{
                if(sprintObject.has("name") ){
                    nomInput.setText(sprintObject.get("name").toString());
                }else{
                    nomInput.setText("");
                }

                if(sprintObject.has("description") ){
                    descrInput.setText(sprintObject.get("description").toString());
                }else{
                    descrInput.setText("");
                }

                if(sprintObject.has("start_date") ){
                    //Date date = dateFormat.parse(projectObject.get("start_date").toString().substring(0,10));
                    String dateStr = parseDate(sprintObject.get("start_date").toString().substring(0,10));
                    startDateInput.setText(dateStr);
                }else{
                    startDateInput.setText("Inicio");
                }

                if(sprintObject.has("estimated_end") ){
                    String dateStr = parseDate(sprintObject.get("estimated_end").toString().substring(0,10));

                    endDateInput.setText(dateStr);
                }else{
                    endDateInput.setText("Fin");
                }

                if(sprintObject.has("end_date") ){
                    String dateStr = parseDate(sprintObject.get("end_date").toString().substring(0,10));
                    endateText.setText(dateStr);
                }else{
                    endateText.setText("No finalizado");
                }

                if(sprintObject.get("status").toString().equals("[]") ){
                    Toast.makeText(c, "No existen estados." ,
                            Toast.LENGTH_SHORT).show();
                }else{

                    statusObject = (JSONArray) sprintObject.get("status");

                    StatusAdapter statusAdapter = new StatusAdapter(c, statusObject);
                    statusListView.setAdapter(statusAdapter);
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
     * Represents an asynchronous task used to update an sprint.
     */
    public class UpdateSprintProjectTask extends AsyncTask<Void, Void, Boolean> {
        private String idSprint = null;
        private String error = "";
        private String name = null;
        private String description = null;
        private String start_date = "vacio";
        private String end_date = "vacio";
        private String estimated_end = "vacio";
        private String username = null;

        UpdateSprintProjectTask( String name, String description, String startDate, String estimatedEnd, boolean finalizado) {

            this.idSprint = Session.getProjectSelected();
            this.name = name;
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
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected()+"/sprints/"+Session.getSprintSelected();
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
            mUpdateSprintTask = null;
            showProgress(false);

            if (success) {
                setResult(RESULT_OK);
                finish();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);

            mUpdateSprintTask = null;
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
            Toast.makeText(c, "Ha ocurrido agún problema." , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Represents an asynchronous task used to add a new status to an sprint.
     */
    public class NewStatusTask extends AsyncTask<Void, Void, Boolean> {

        private String name = null;
        NewStatusTask( String name) {
            this.name = name;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected()+"/sprints/"+Session.getSprintSelected()+"/status";
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{
                if(name != null){
                    body.put("name", name);
                }

                URL url = new URL(urlPedida);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
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
            mNewStatusTask = null;
            showProgress(false);

            if (success) {
                updateActivity();
            } else {
                Toast.makeText(SprintUpdateActivity.this, "No se ha podido crear el estado." ,
                        Toast.LENGTH_SHORT).show();            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);

            mNewStatusTask = null;
        }

    }
}
