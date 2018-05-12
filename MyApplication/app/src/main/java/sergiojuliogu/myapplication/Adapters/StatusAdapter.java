package sergiojuliogu.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sergiojuliogu.myapplication.R;

public class StatusAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray statusArray;

    // 1
    public StatusAdapter(Context context, JSONArray status) {
        this.mContext = context;
        this.statusArray = status;
    }

    // 2
    @Override
    public int getCount() {
        return statusArray.length();
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
            final JSONObject statusSprint = statusArray.getJSONObject(position);
            final JSONArray tasksObject = statusSprint.getJSONArray("tasks");

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_status, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.nom_tareas_status);
            final TextView numTextView = (TextView)convertView.findViewById(R.id.num_tareas_status);
            // 4

            String name = statusSprint.get("name").toString();
            nameTextView.setText(name);

            String num = String.valueOf(tasksObject.length());
            numTextView.setText(num);

        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }


        return convertView;
    }

}