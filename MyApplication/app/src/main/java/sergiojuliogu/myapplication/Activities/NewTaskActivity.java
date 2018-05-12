package sergiojuliogu.myapplication.Activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

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

public class NewTaskActivity extends AppCompatActivity {

    private NewStatusTask mNewStatusTask;

    private ImageButton startDate;
    private ImageButton endDate;

    //UI References
    private EditText startDateInput;
    private EditText endDateInput;

    private EditText nomInput;
    private EditText descrInput;

    private Button newTaskButton;
    private Button colorTaskButton;
    private View mProgressView;
    private String colorElegido = "FFFFFF";

    private String statusID;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        if(!Session.getLoged()){
            finish();
        }

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){
            value = b.getString("key");
            statusID = value;
        }

        findViewsById();

        setDateTimeField();

    }

    private void findViewsById() {
        startDateInput = (EditText) findViewById(R.id.etxt_fromdate_tarea);
        startDateInput.setInputType(InputType.TYPE_NULL);
        startDateInput.requestFocus();

        endDateInput = (EditText) findViewById(R.id.etxt_todate_tarea);
        endDateInput.setInputType(InputType.TYPE_NULL);

        startDate = (ImageButton) findViewById(R.id.imageButton_task_new);
        endDate = (ImageButton) findViewById(R.id.imageButton2_task_new);

        nomInput = (EditText) findViewById(R.id.input_nombre_tarea);
        descrInput = (EditText) findViewById(R.id.input_description_task);

        newTaskButton = (Button) findViewById(R.id.new_task_button);
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptNewTask();
            }
        });

        final ColorPicker cp = new ColorPicker(NewTaskActivity.this, 125, 125, 125);

        colorTaskButton = (Button) findViewById(R.id.newColor);
        colorTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Show color picker dialog */
                cp.show();

                //cp.enableAutoClose(); // Enable auto-dismiss for the dialog

                /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        // Do whatever you want
                        // Examples
                        Log.d("Alpha", Integer.toString(Color.alpha(color)));
                        Log.d("Red", Integer.toString(Color.red(color)));
                        Log.d("Green", Integer.toString(Color.green(color)));
                        Log.d("Blue", Integer.toString(Color.blue(color)));

                        Log.i("Pure Hex", Integer.toHexString(color));
                        Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                        Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));
                        colorElegido = Integer.toHexString(color);
                        Log.i("elegido", colorElegido);

                        // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                        // cp.dismiss();
                    }
                });
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
    private void attemptNewTask(){
        if (mNewStatusTask != null) {
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

            //showProgress(true);
            mNewStatusTask = new NewStatusTask(nameSprint , descriptionSprint , startSprint, endSprint, colorElegido);
            mNewStatusTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous register task used to create new sprint.
     */
    public class NewStatusTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mDescription;
        private final String mStartDate;
        private final String mEndDate;
        private final String mColor;
        private  String error;

        NewStatusTask(String nombre,String descripcion, String fechaInicio, String fechaFin, String color ) {
            mName = nombre;
            mDescription = descripcion;
            mStartDate = fechaInicio;
            mEndDate = fechaFin;
            mColor = color;
            error = "false";
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida =  Session.URL+"/users/"+Session.getUsername()+"/projects/"+Session.getProjectSelected()+"/sprints/"+Session.getSprintSelected()+"/status/"+statusID+"/tasks";
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
                body.put("color", mColor);


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
            mNewStatusTask = null;
            //showProgress(false);

            if (success) {
                setResult(RESULT_OK);
                Toast.makeText(NewTaskActivity.this, "Se ha creado la tarea." ,
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mostrarErroresRespuesta();
            }
        }

        @Override
        protected void onCancelled() {
            mNewStatusTask = null;
            //showProgress(false);
        }

        private void mostrarErroresRespuesta(){
            Toast.makeText(NewTaskActivity.this, "Ha ocurrido algún problema." ,
                    Toast.LENGTH_SHORT).show();

        }
    }
}
