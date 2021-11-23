package it.ltm.scp.module.android.ui;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import it.ltm.scp.module.android.R;

/**
 * Created by HW64 on 13/10/2016.
 */
public class DialogEventAdapter extends ArrayAdapter<String> {

    private List<String> messages;
    private LayoutInflater mLayoutInflater;


    public DialogEventAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        messages = objects;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public String getItem(int position) {
        return messages.get(position);
    }

    public void updateItems(Collection<String> items){
        messages.clear();
        messages.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.dialog_list_row, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.mText = (TextView) convertView.findViewById(R.id.dialog_list_row_text);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        String item = getItem(position);
        holder.mText.setText(item);
        return convertView;
    }

    class ViewHolder {
        TextView mText;
    }
}
