package kr.kaist.resl.kitchenhublauncher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.Recall;

/**
 * Created by nicolais on 5/24/15.
 *
 * Notification adapter for notification list on Home Screen
 */
public class NotificationAdapter extends ArrayAdapter {

    private LayoutInflater inflater;

    private List<Recall> objects = new ArrayList<Recall>();

    public NotificationAdapter(Context context) {
        super(context, -1);
        inflater = LayoutInflater.from(context);

        loadNotifications();
    }

    /**
     * Reloads notifications
     */
    public void loadNotifications() {
        objects.clear();
        objects.addAll(DBUtil.getRecallNotifications(getContext()));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.element_notification, null);
        }

        Recall r = (Recall) getItem(position);

        // Get Company and Item name of recalled product
        String[] names = DBUtil.getRecallNames(getContext(), r.getId());

        // Set item name
        ((TextView) view.findViewById(R.id.element_text1)).setText(names[1]);
        // Set company name
        ((TextView) view.findViewById(R.id.element_text2)).setText(names[0]);

        return view;
    }
}
