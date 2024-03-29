package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class NewProjectActivity extends AppCompatActivity {

    private NewProjectTask mNewProjectTask;

    private ImageButton startDate;
    private ImageButton endDate;

    //UI References
    private EditText startDateInput;
    private EditText endDateInput;

    private EditText nomInput;
    private EditText descrInput;
    private EditText repoInput;

    private Button newProjButton;
    private View mProgressView;
    private View mLoginFormView;
    private View mBotonCrearProyecto;


    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        findViewsById();

        setDateTimeField();
    }

    private void findViewsById() {

        mProgressView = findViewById(R.id.new_project_progress);
        mLoginFormView = findViewById(R.id.new_project_form);
        mBotonCrearProyecto = findViewById(R.id.view_boton_crear_proyecto);

        startDateInput = (EditText) findViewById(R.id.etxt_fromdate);
        startDateInput.setInputType(InputType.TYPE_NULL);
        startDateInput.requestFocus();

        endDateInput = (EditText) findViewById(R.id.etxt_todate);
        endDateInput.setInputType(InputType.TYPE_NULL);

        startDate = (ImageButton) findViewById(R.id.imageButton_sprint_update);
        endDate = (ImageButton) findViewById(R.id.imageButton2_sprint_update);

        nomInput = (EditText) findViewById(R.id.input_nombre);
        descrInput = (EditText) findViewById(R.id.input_description);
        repoInput = (EditText) findViewById(R.id.input_respository);

        newProjButton = (Button) findViewById(R.id.new_project_button);
        newProjButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptNewProject();
            }
        });

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

            mBotonCrearProyecto.setVisibility(show ? View.GONE : View.VISIBLE);
            mBotonCrearProyecto.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBotonCrearProyecto.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mBotonCrearProyecto.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void attemptNewProject(){
        if (mNewProjectTask != null) {
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

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
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

            showProgress(true);
            mNewProjectTask = new NewProjectTask(nameProject , descriptionProject , repositoryProject, startProject, endProject);
            mNewProjectTask.execute((Void) null);
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
     * Represents an asynchronous register task used to create new project
     */
    public class NewProjectTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mDescription;
        private final String mRepository;
        private final String mStartDate;
        private final String mEndDate;
        private  String error;

        NewProjectTask(String nombre,String descripcion, String repositorio, String fechaInicio, String fechaFin ) {
            mName = nombre;
            mDescription = descripcion;
            mRepository = repositorio;
            mStartDate = fechaInicio;
            mEndDate = fechaFin;
            error = "false";
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida =  Session.URL+"/users/"+Session.getUsername()+"/projects";
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{

                body.put("name", mName);
                body.put("description", mDescription);
                body.put("repository", mRepository);

                if(!mStartDate.equals("")){
                    body.put("start_date", mStartDate);

                }
                if(!mEndDate.equals("")){
                    body.put("estimated_end", mEndDate);
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

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.i("exception", e.toString());
            } catch (Exception e){
                Log.i("exception", e.toString());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mNewProjectTask = null;
            showProgress(false);

            if (success) {
                setResult(RESULT_OK);
                Toast.makeText(NewProjectActivity.this, "Se ha creado el proyecto." ,
                        Toast.LENGTH_SHORT).show();

                finish();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mNewProjectTask = null;
            showProgress(false);
        }

        private void mostrarErroresRespuesta(){

            if(error.equals("repositorio")){
                repoInput.setError("URL no válida. Ejemplo: www.ejemplo.com/...");
                repoInput.requestFocus();

            }else{
                Toast.makeText(NewProjectActivity.this, "Ha ocurrido algún problema." ,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

}