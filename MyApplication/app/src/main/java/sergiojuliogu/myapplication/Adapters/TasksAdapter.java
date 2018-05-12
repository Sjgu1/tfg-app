package sergiojuliogu.myapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import sergiojuliogu.myapplication.R;
import sergiojuliogu.myapplication.Session;

public class TasksAdapter extends BaseAdapter {
    private final Context mContext;
    private JSONArray tasksArray;
    private final String idStatus;

    //private StatusGetInfo mStatusGetInfo;
    // 1
    public TasksAdapter(Context context,String idStatus, JSONArray tasks) {
        //mStatusGetInfo = new StatusGetInfo(idStatus);
        //mStatusGetInfo.execute((Void) null);
        this.tasksArray = tasks;
        this.mContext = context;
        this.idStatus = idStatus;

    }

    // 2
    @Override
    public int getCount() {
        return tasksArray.length();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        try{
            final JSONObject taskObject = tasksArray.getJSONObject(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_tasks, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.nombreTarea);

            final String name = taskObject.get("name").toString();
            nameTextView.setText(name);
            // 4
            final CardView colorTarjeta = (CardView)convertView.findViewById(R.id.colorTarea);
            int color = Color.parseColor("#"+taskObject.getString("color"));
            colorTarjeta.setCardBackgroundColor(color);


        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }

        return convertView;
    }


}
