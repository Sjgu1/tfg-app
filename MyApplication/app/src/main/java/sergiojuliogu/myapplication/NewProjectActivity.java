package sergiojuliogu.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class NewProjectActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    int from_year, from_month, from_day,to_year, to_month, to_day;
    private DatePickerDialog.OnDateSetListener from_dateListener,to_dateListener;
    private ImageButton startDate;
    private ImageButton endDate;

    public final int DATE_PICKER_TO = 0;
    public final int DATE_PICKER_FROM = 1;

    private int calendario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        dateView = (TextView) findViewById(R.id.input_start_date);
        calendar = Calendar.getInstance();

        from_year = calendar.get(Calendar.YEAR);
        from_month = calendar.get(Calendar.MONTH);
        from_day = calendar.get(Calendar.DAY_OF_MONTH) +1;

        to_year = calendar.get(Calendar.YEAR);
        to_month = calendar.get(Calendar.MONTH);
        to_day = calendar.get(Calendar.DAY_OF_MONTH) +1;

        startDate = (ImageButton)findViewById(R.id.imageButton) ;
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
                calendario=0;

                //crearCalendario(0);
            }
        });

        endDate = (ImageButton)findViewById(R.id.imageButton2) ;
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // crearCalendario(1);
                calendario=1;
                setDateEnd(v);
            }
        });

        from_dateListener = new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                dateView.setText(new StringBuilder().append(from_day).append("/")
                        .append(from_month).append("/").append(from_year));
            }
        };


        to_dateListener = new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                dateView.setText(new StringBuilder().append(from_day).append("/")
                        .append(from_month).append("/").append(from_year));
            }
        };


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    public void setDateEnd(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch(id){
            case DATE_PICKER_FROM :
                return new DatePickerDialog(this, from_dateListener, from_year, from_month, from_day);
            case DATE_PICKER_TO :
                return new DatePickerDialog(this, to_dateListener, to_year, to_month, to_day);
        }
        return null;
    }


}
