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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sergiojuliogu.myapplication.R;

public class ChangesAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray changes;

    // 1
    public ChangesAdapter(Context context, JSONArray changes) {
        this.mContext = context;
        this.changes = changes;
    }

    // 2
    @Override
    public int getCount() {
        return changes.length();
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
        try {
            final JSONObject changeObject = changes.getJSONObject(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_change, null);
            }

            // 3
            final TextView dateView = (TextView) convertView.findViewById(R.id.change_date);
            final TextView messageView = (TextView) convertView.findViewById(R.id.change_text);

            // 4

            String date = changeObject.get("created_at").toString().substring(0,10);
            date = parseDate(date);
            dateView.setText(date);

            String message = changeObject.get("message").toString();
            messageView.setText(message);

        } catch (JSONException e) {
            Log.e("JavaException", e.toString());
        }


        return convertView;
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
