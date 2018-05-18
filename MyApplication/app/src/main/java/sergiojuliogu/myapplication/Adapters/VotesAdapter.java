package sergiojuliogu.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sergiojuliogu.myapplication.R;

public class VotesAdapter  extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<String> items = new ArrayList<String>();

    // 1
    public VotesAdapter(Context context) {
        this.mContext = context;
        items.add("1");
        items.add("2");
        items.add("3");
        items.add("5");
        items.add("8");
        items.add("13");
        items.add("20");
        items.add("40");
        items.add("100");
    }

    // 2
    @Override
    public int getCount() {
        return items.size();
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

        final String number = items.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_poker, null);
        }

        // 3
        final TextView numberTextView = (TextView)convertView.findViewById(R.id.poker_number);

        // 4

        numberTextView.setText(number);


        return convertView;
    }
}
