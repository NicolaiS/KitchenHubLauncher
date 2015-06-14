package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import constants.database.KHSchema;
import constants.database.KHSchemaDatasourceContainer;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.Content_URIs;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.Container;
import models.DataSource;
import models.DataSourceContainerRelation;

/**
 * Created by nicolais on 4/27/15.
 * <p/>
 * Dialog used to link Container and Data Source
 */
public class AddDSCRelationDialog extends Dialog {

    // Views
    private Spinner spinnerDatasource;
    private Spinner spinnerAntenna;

    // Container to be linked
    private Container localContainer;

    /**
     * @param context       context
     * @param postSuccess   Runnable to be executed if container is successfully linked
     * @param container     Container to be linked
     * @param tempRelations Set of temporary relations
     */
    public AddDSCRelationDialog(Context context, final Runnable postSuccess, Container container, final List<DataSourceContainerRelation> tempRelations) {
        super(context);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_dsc_create);

        localContainer = container;

        spinnerDatasource = (Spinner) findViewById(R.id.datasource_select);
        DataSourceAdapter dsAdapter = new DataSourceAdapter(getContext());
        spinnerDatasource.setAdapter(dsAdapter);

        spinnerAntenna = (Spinner) findViewById(R.id.antenna_select);
        AntennaAdapter antennaAdapter = new AntennaAdapter(getContext());
        spinnerAntenna.setAdapter(antennaAdapter);

        /**
         * On accept
         * Save relations, execute postSuccess runnable and dismiss.
         */

        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSourceContainerRelation dsc = new DataSourceContainerRelation(null, (DataSource) spinnerDatasource.getSelectedItem(), localContainer, (Integer) spinnerAntenna.getSelectedItem());
                if (!checkRelation(dsc)) return;
                tempRelations.add(dsc);
                postSuccess.run();
                dismiss();
            }
        });
    }

    /**
     * Check if relations is valid. Meaning data source / antenna pair is not linked to another container and another data source is not linked to relevant container.
     *
     * @param dsc relation to be checked
     * @return True if relation is valid. False if not.
     */
    private boolean checkRelation(DataSourceContainerRelation dsc) {
        ContentResolver resolver = getContext().getContentResolver();

        // Check if data source / antenna pair is not linked to another container
        String selection = KHSchemaDatasourceContainer.CN_DATA_SOURCE_ID + " = ? AND " + KHSchemaDatasourceContainer.CN_ANTENNA + " = ?";
        String[] selectionArgs = new String[]{Integer.toString(dsc.getDatasource().getId()), Integer.toString(dsc.getAntenna())};
        Cursor c = resolver.query(Content_URIs.CONTENT_URI_DSC, new String[]{KHSchema.CN_ID}, selection, selectionArgs, null);

        if (c.moveToFirst()) {
            c.close();
            Toast.makeText(getContext(), getContext().getString(R.string.error_dsc_one), Toast.LENGTH_SHORT).show();
            return false;
        }
        c.close();

        // Check if another data source is not linked to relevant container.
        if (dsc.getContainer() != null) {
            selection = KHSchemaDatasourceContainer.CN_CONTAINER_ID + " = ? AND " + KHSchemaDatasourceContainer.CN_DATA_SOURCE_ID + " != ?";
            selectionArgs = new String[]{Integer.toString(dsc.getContainer().getId()), Integer.toString(dsc.getDatasource().getId())};

            c = resolver.query(Content_URIs.CONTENT_URI_DSC, new String[]{KHSchema.CN_ID}, selection, selectionArgs, null);

            if (c.moveToFirst()) {
                c.close();
                Toast.makeText(getContext(), getContext().getString(R.string.error_dsc_two), Toast.LENGTH_SHORT).show();
                return false;
            }
            c.close();
        }

        return true;
    }

    // Data source spinner adapter
    class DataSourceAdapter extends ArrayAdapter {

        private List<DataSource> values;
        private LayoutInflater inflater;

        public DataSourceAdapter(Context context) {
            super(context, R.layout.spinner_primary, R.id.text);

            // Load data sources
            values = DBUtil.getDataSources(context);

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_primary, null);
            }

            DataSource ds = (DataSource) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(ds.getIpAddress());

            return view;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_base_dropdown, null);
            }

            DataSource ds = (DataSource) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(ds.getIpAddress());

            return view;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Object getItem(int position) {
            return values.get(position);
        }

    }

    class AntennaAdapter extends ArrayAdapter {

        private Integer[] values;
        private LayoutInflater inflater;

        public AntennaAdapter(Context context) {
            super(context, R.layout.spinner_primary, R.id.text);

            values = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_primary, null);
            }

            Integer antenna = (Integer) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(Integer.toString(antenna));

            return view;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_base_dropdown, null);
            }

            Integer antenna = (Integer) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(Integer.toString(antenna));

            return view;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public Object getItem(int position) {
            return values[position];
        }

    }
}
