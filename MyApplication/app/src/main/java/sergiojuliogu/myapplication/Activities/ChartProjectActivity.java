package sergiojuliogu.myapplication.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sergiojuliogu.myapplication.Adapters.UsersAdapter;
import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class ChartProjectActivity extends AppCompatActivity {
    private JSONObject projectObject;
    private JSONArray usersObject;

    private View mProgressView;
    private View mLoginFormView;

    private LineChart chart;

    private ProjectInfoTask mProjectTask = null;
    List<Entry> entries = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_project);
        findViewsById();
        mProjectTask = new ProjectInfoTask();
        mProjectTask.execute((Void)null);

    }

    private void findViewsById() {
        chart = (LineChart) findViewById(R.id.chart);

        mLoginFormView = findViewById(R.id.chart_project_form);
        mProgressView = findViewById(R.id.chart_project_progress);

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
     * Represents an asynchronous task used to get project info information.
     */
    public class ProjectInfoTask extends AsyncTask<Void, Void, Boolean> {
        private String project;
        private String projectObtenido;

        ProjectInfoTask() {
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            //Some url endpoint that you may have
            String urlPedida = Session.URL+"/users/" + Session.getUsername()+"/projects/"+Session.getProjectSelected();
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
                Toast.makeText(ChartProjectActivity.this, "Error al obtener los datos del proyecto." ,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mProjectTask = null;
        }

        private void pintarDatos(){
            try{
                String start = null;
                String end = null;
                String stimated = null;
                String lastDate = null;


                if(projectObject.has("start_date") ){
                    start= parseDate(projectObject.get("start_date").toString().substring(0,10));
                }

                if(projectObject.has("estimated_end") ){
                    stimated = parseDate(projectObject.get("estimated_end").toString().substring(0,10));
                }

                if(projectObject.has("end_date") ){
                    end = parseDate(projectObject.get("end_date").toString().substring(0,10));
                }

                boolean continuar = false;
                LineData lineData = new LineData();
                LineDataSet dataSet;

                if(start != null && stimated != null){
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextSize(10f);
                    xAxis.setTextColor(Color.RED);
                    xAxis.setDrawAxisLine(true);
                    xAxis.setDrawGridLines(false);
                    xAxis.setValueFormatter(new DayAxisValueFormatter(chart));
                    xAxis.setAxisMinimum(calcularDias(start));
                    entries.add(new Entry(calcularDias(start), 100));
                    entries.add(new Entry(calcularDias(stimated),0));
                    dataSet = new LineDataSet(entries, "Evolución ideal");
                    continuar = true;
                }else {
                    Toast.makeText(ChartProjectActivity.this, "El proyecto no tiene datos suficientes." ,
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                JSONArray  arrays= projectObject.getJSONArray("sprints");
                List<Entry> entriesSprints = new ArrayList<Entry>();
                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

                if (arrays.length() != 0 && continuar){
                    JSONObject sprintLeido;
                    entriesSprints.add(new Entry(calcularDias(start), 100));
                    float porcentaje = 100/arrays.length();
                    for(int i = 0; i< arrays.length(); i++){
                        float lastEntry = 100;
                        end = null;
                        sprintLeido=  arrays.getJSONObject(i);
                        if(sprintLeido.has("end_date")){
                            end = parseDate(sprintLeido.get("end_date").toString().substring(0,10));
                            entriesSprints.add(new Entry(calcularDias(end), (100- porcentaje* (i + 1))));
                            lastEntry = (100- porcentaje*(i+1));
                            start = sprintLeido.get("end_date").toString();
                        }else{
                            //String fecha = parseDate(start.toString().substring(0,10));
                           // entriesSprints.add(new Entry(calcularDias(fecha), lastEntry));
                        }
                    }
                    LineDataSet dataSet2 = new LineDataSet(entriesSprints, "Evolución");
                    dataSet2.setColor(Color.RED);
                    dataSet2.setCircleColor(Color.RED);
                    lineData.addDataSet(dataSet);
                    lineData.addDataSet(dataSet2);
                    chart.setData(lineData);
                    chart.invalidate();

                }else if(continuar){
                    lineData.addDataSet(dataSet);

                    chart.setData(lineData);
                    chart.invalidate();
                }


            }catch (JSONException e){
                Log.e("JSONException", e.toString());
            }

        }
        public int calcularDias(String date){
            System.out.println(date);
            String[] output = date.split("/");
            int dias = 0;
            boolean bisiesto = false;
            String day = output[1];
            String month = output[0];
            String year = output[2];
            if(year.equals("2016") || year.equals("2020")){
                bisiesto = true;
            }

            switch (year) {
                case "2016":
                    dias += 0;
                    break;
                case "2017":
                    dias += 366;
                    break;
                case "2018":
                    dias += 731;
                    break;
                case "2019":
                    dias += 1094;
                    break;
                case "2020":
                    dias += 1461;
                    break;
                case "2021":
                    dias += 1827;
                    break;
                case "2022":
                    dias += 2192;
                    break;
                case "2023":
                    dias += 2557;
                    break;
            }

            if(month.equals("01") ){
               dias +=0;
            }else if(month.equals("02") ){
                dias += 31;
            }else{
                if(bisiesto){
                    dias +=1;
                }
                if(month.equals("03")){
                    dias += 56;
                }
                if(month.equals("04")){
                    dias+= 90;
                }
                if(month.equals("05")){
                    dias += 120;
                }
                if(month.equals("06")){
                    dias += 151;
                }
                if(month.equals("07")){
                    dias += 181;
                }
                if(month.equals("08")){
                    dias += 212;
                }
                if(month.equals("09")){
                    dias += 243;
                }
                if(month.equals("10")){
                    dias += 273;
                }
                if(month.equals("11")){
                    dias += 304;
                }
                if(month.equals("12")){
                    dias += 334;
                }
            }
            dias += Integer.parseInt(day);
            return dias;
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

