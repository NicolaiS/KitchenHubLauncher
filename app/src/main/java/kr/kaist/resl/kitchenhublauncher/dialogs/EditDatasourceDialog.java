package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;

import enums.EnumDataSourceType;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.BasicUtils;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.DataSource;

/**
 * Created by nicolais on 4/27/15.
 * <p/>
 * Dialog to create/delete/update Data Source
 */
public class EditDatasourceDialog extends Dialog {

    private EditText editText;
    private Spinner spinner;

    private DataSource localDataSource;

    /**
     * @param context     context
     * @param postSuccess Runnable to be run if changes are executed
     * @param dataSource  data source to be edited. Null will create new data source
     */
    public EditDatasourceDialog(Context context, final Runnable postSuccess, DataSource dataSource) {
        super(context);

        localDataSource = dataSource;

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_datasource_conf);

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText = (EditText) findViewById(R.id.reader_hostname);
        spinner = (Spinner) findViewById(R.id.reader_type);

        // Load data source for editing if provided
        if (localDataSource != null) {
            editText.setText(localDataSource.getIpAddress());
            spinner.post(new Runnable() {
                @Override
                public void run() {
                    spinner.setSelection(localDataSource.getDatasourceType().id - 1);
                }
            });

            findViewById(R.id.delete).setVisibility(View.VISIBLE);
            findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBUtil.deleteDataSource(getContext(), localDataSource.getId());
                    postSuccess.run();
                    dismiss();
                }
            });
        } else {
            findViewById(R.id.delete).setVisibility(View.GONE);
        }

        DataSourceAdapter adapter = new DataSourceAdapter(getContext());
        adapter.setDropDownViewResource(R.layout.spinner_base_dropdown);
        spinner.setAdapter(adapter);

        /**
         * On accept click
         * Check entered values.
         * Save, execute postSuccess and dismiss if values are valid.
         */
        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hostname = editText.getText().toString().trim();
                if (InetAddressUtils.isIPv4Address(hostname)) {
                    Integer typeId = ((EnumDataSourceType) spinner.getSelectedItem()).id;
                    if (localDataSource != null) {
                        DBUtil.updateDataSource(getContext(), localDataSource.getId(), hostname, typeId);
                    } else {
                        DBUtil.addDataSource(getContext(), hostname, typeId);
                    }
                    postSuccess.run();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.error_not_ip), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Data Source type spinner adapter
    class DataSourceAdapter extends ArrayAdapter {

        private EnumDataSourceType[] values;
        private LayoutInflater inflater;

        public DataSourceAdapter(Context context) {
            super(context, R.layout.spinner_primary, R.id.text);

            values = EnumDataSourceType.values();

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_primary, null);
            }

            EnumDataSourceType item = (EnumDataSourceType) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(BasicUtils.getStringResourceByName(getContext(), item.name));

            return view;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_base_dropdown, null);
            }

            EnumDataSourceType item = (EnumDataSourceType) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(BasicUtils.getStringResourceByName(getContext(), item.name));

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
