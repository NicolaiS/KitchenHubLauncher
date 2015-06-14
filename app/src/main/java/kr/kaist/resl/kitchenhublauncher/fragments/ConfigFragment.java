package kr.kaist.resl.kitchenhublauncher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import kr.kaist.resl.kitchenhublauncher.Constants;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.dialogs.EditContainerDialog;
import kr.kaist.resl.kitchenhublauncher.dialogs.EditDatasourceDialog;
import kr.kaist.resl.kitchenhublauncher.utils.BasicUtils;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.Container;
import models.DataSource;

/**
 * Created by nicolais on 15. 3. 25.
 */
public class ConfigFragment extends Fragment {

    private LinearLayout readerContainer;
    private LinearLayout containerContainer;

    public static final ConfigFragment newInstance() {
        return new ConfigFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_config, null);

        readerContainer = (LinearLayout) v.findViewById(R.id.config_reader_container);
        containerContainer = (LinearLayout) v.findViewById(R.id.config_container_container);

        updateContainers();

        v.findViewById(R.id.config_add_data_source).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditDatasourceDialog(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        updateContainers();
                    }
                }, null).show();
            }
        });

        v.findViewById(R.id.config_add_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditContainerDialog(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        updateContainers();
                    }
                }, null).show();
            }
        });

        return v;
    }

    private void updateContainers() {
        readerContainer.removeAllViews();
        containerContainer.removeAllViews();

        final Runnable postSuccess = new Runnable() {
            @Override
            public void run() {
                updateContainers();
            }
        };

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        List<DataSource> readers = DBUtil.getDataSources(getActivity());
        for (final DataSource r : readers) {
            View v = inflater.inflate(R.layout.element_two_text_base, null);

            ((TextView) v.findViewById(R.id.element_text1)).setText(r.getIpAddress());
            try {
                ((TextView) v.findViewById(R.id.element_text2)).setText(BasicUtils.getStringResourceByName(getActivity(), r.getDatasourceType().name));
            } catch (Exception e) {
                Log.w(getClass().getName(), e.getMessage());
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EditDatasourceDialog(getActivity(), postSuccess, r).show();
                }
            });

            readerContainer.addView(v);
        }

        List<Container> containers = DBUtil.getContainers(getActivity());
        for (final Container c : containers) {
            View v = inflater.inflate(R.layout.element_two_text_base, null);

            ((TextView) v.findViewById(R.id.element_text1)).setText(c.getName());
            try {
                ((TextView) v.findViewById(R.id.element_text2)).setText(BasicUtils.getStringResourceByName(getActivity(), c.getType().name));
            } catch (Exception e) {
                Log.w(getClass().getName(), e.getMessage());
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new EditContainerDialog(getActivity(), postSuccess, c).show();
                }
            });

            containerContainer.addView(v);
        }

        getActivity().sendBroadcast(new Intent(Constants.BROADCAST_CONTAINER_CHANGE));
    }

}
