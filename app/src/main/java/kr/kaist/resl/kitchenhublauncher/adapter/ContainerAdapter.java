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
import kr.kaist.resl.kitchenhublauncher.utils.BasicUtils;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.Container;

/**
 * Created by nicolais on 5/24/15.
 *
 * Container adapter for Home Screen container listview
 */
public class ContainerAdapter extends ArrayAdapter {

    private LayoutInflater inflater;

    private List<Container> objects = new ArrayList<Container>();

    public ContainerAdapter(Context context) {
        super(context, -1);
        inflater = LayoutInflater.from(context);

        loadContainers();
    }

    /**
     * Reload containers
     */
    private void loadContainers() {
        objects.clear();

        // Add "combined" option
        objects.add(new Container(null));

        objects.addAll(DBUtil.getContainers(getContext()));
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
            view = inflater.inflate(R.layout.element_two_text_base, null);
        }

        Container c = (Container) getItem(position);

        if (c.getId() != null) {
            // Normal container
            ((TextView) view.findViewById(R.id.element_text1)).setText(c.getName());
            ((TextView) view.findViewById(R.id.element_text2)).setText(BasicUtils.getStringResourceByName(getContext(), c.getType().name));
            view.findViewById(R.id.element_text1).setVisibility(View.VISIBLE);
            view.findViewById(R.id.element_text2).setVisibility(View.VISIBLE);
            view.findViewById(R.id.element_text3).setVisibility(View.GONE);
        } else {
            // "Combined" container
            ((TextView) view.findViewById(R.id.element_text3)).setText(getContext().getString(R.string.container_combined));
            view.findViewById(R.id.element_text1).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.element_text2).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.element_text3).setVisibility(View.VISIBLE);
        }


        return view;
    }
}
