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

public class NewSprintActivity extends AppCompatActivity {


    private NewSprintTask mNewSprintTask;

    private ImageButton startDate;
    private ImageButton endDate;

    //UI References
    private EditText startDateInput;
    private EditText endDateInput;

    private EditText nomInput;
    private EditText descrInput;

    private Button newSprintButton;

    private View mProgressView;
    private View mLoginFormView;
    private View mBoton;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sprint);

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        findViewsById();

        setDateTimeField();
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



            mBoton.setVisibility(show ? View.GONE : View.VISIBLE);
            mBoton.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBoton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mBoton.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void findViewsById() {
        mLoginFormView = findViewById(R.id.new_sprint_form);
        mProgressView = findViewById(R.id.new_sprint_progress);
        mBoton = findViewById(R.id.view_boton_crear_sprint);

        startDateInput = (EditText) findViewById(R.id.etxt_fromdate_sprint);
        startDateInput.setInputType(InputType.TYPE_NULL);
        startDateInput.requestFocus();

        endDateInput = (EditText) findViewById(R.id.etxt_todate_sprint);
        endDateInput.setInputType(InputType.TYPE_NULL);

        startDate = (ImageButton) findViewById(R.id.imageButton_sprint_update);
        endDate = (ImageButton) findViewById(R.id.imageButton2_sprint_update);

        nomInput = (EditText) findViewById(R.id.input_nombre_sprint);
        descrInput = (EditText) findViewById(R.id.input_description_sprint);

        newSprintButton = (Button) findViewById(R.id.update_sprint_button);
        newSprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptNewSprint();
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

    private void attemptNewSprint(){
        if (mNewSprintTask != null) {
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

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
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
            mNewSprintTask = new NewSprintTask(nameSprint , descriptionSprint , startSprint, endSprint);
            mNewSprintTask.execute((Void) null);
        }
    }
    /**
     * Represents an asynchronous register task used to create new sprint.
     */
    public class NewSprintTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mDescription;
        private final String mStartDate;
        private final String mEndDate;
        private  String error;

        NewSprintTask(String nombre,String descripcion, String fechaInicio, String fechaFin ) {
            mName = nombre;
            mDescription = descripcion;
            mStartDate = fechaInicio;
            mEndDate = fechaFin;
            error = "false";
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida =  Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected()+"/sprints";
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            //Perform the doInBackground method, passing in our url

            JSONObject body = new JSONObject();
            try{

                body.put("name", mName);
                body.put("description", mDescription);

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
                    return false;
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
            mNewSprintTask = null;
            showProgress(false);

            if (success) {
                setResult(RESULT_OK);
                Toast.makeText(NewSprintActivity.this, "Se ha creado el sprint." ,
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mNewSprintTask = null;
            showProgress(false);
        }

        private void mostrarErroresRespuesta(){

            Toast.makeText(NewSprintActivity.this, "Ha ocurrido algún problema." ,
                    Toast.LENGTH_SHORT).show();

        }
    }
}
