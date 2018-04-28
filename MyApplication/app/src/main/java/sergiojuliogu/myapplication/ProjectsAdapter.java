package sergiojuliogu.myapplication;

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

public class ProjectsAdapter extends BaseAdapter {

    private final Context mContext;
    private final JSONArray projects;

    // 1
    public ProjectsAdapter(Context context, JSONArray projects) {
        this.mContext = context;
        this.projects = projects;
    }

    // 2
    @Override
    public int getCount() {
        return projects.length();
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
            final JSONObject project = projects.getJSONObject(position);

            // 2
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.linearlayout_project, null);
            }

            // 3
            final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_project_name);

            // 4
            String name = project.get("name").toString();

            nameTextView.setText(name);
        }catch (JSONException e){
            Log.e("JavaException", e.toString());
        }


        return convertView;
    }

}