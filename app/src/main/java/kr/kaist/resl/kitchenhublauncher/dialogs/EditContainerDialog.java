package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import enums.EnumContainerType;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.BasicUtils;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.Container;
import models.DataSourceContainerRelation;

/**
 * Created by nicolais on 4/27/15.
 * <p/>
 * Dialog to create/update/delete containers
 */
public class EditContainerDialog extends Dialog {

    // Views
    private EditText editText;
    private Spinner spinner;
    private ListView relationList;

    // Adapters
    private ContainerAdapter containerAdapter;
    private RelationAdapter relationAdapter;

    private Container localContainer;

    // Current relations
    private List<DataSourceContainerRelation> relations = new ArrayList<DataSourceContainerRelation>();

    // New relations to be added
    private List<DataSourceContainerRelation> tempRelations = new ArrayList<DataSourceContainerRelation>();

    // Relations to be deleted
    private List<DataSourceContainerRelation> relationsToBeDeleted = new ArrayList<DataSourceContainerRelation>();

    /**
     * @param context     context
     * @param postSuccess Runnable to be run if changes are executed
     * @param container   container to be edited. Null will create new container
     */
    public EditContainerDialog(Context context, final Runnable postSuccess, Container container) {
        super(context);

        localContainer = container;

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_container_conf);

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText = (EditText) findViewById(R.id.container_name);
        spinner = (Spinner) findViewById(R.id.container_type);
        relationList = (ListView) findViewById(R.id.container_ass_readers);

        // Load container for editing if provided
        if (localContainer != null) {
            editText.setText(localContainer.getName());
            spinner.post(new Runnable() {
                @Override
                public void run() {
                    spinner.setSelection(localContainer.getType().id - 1);
                }
            });


            relations = DBUtil.getRelatedDataSources(getContext(), localContainer);

            findViewById(R.id.delete).setVisibility(View.VISIBLE);
            findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBUtil.deleteContainer(getContext(), localContainer.getId());
                    postSuccess.run();
                    dismiss();
                }
            });
        } else {
            findViewById(R.id.delete).setVisibility(View.GONE);
        }

        containerAdapter = new ContainerAdapter(getContext());
        spinner.setAdapter(containerAdapter);

        relationAdapter = new RelationAdapter(getContext());
        relationList.setAdapter(relationAdapter);

        /**
         * On accept click
         * Check entered values.
         * Save, execute postSuccess and dismiss if values are valid.
         */
        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString().trim();
                if (!name.isEmpty()) {
                    Integer typeId = ((EnumContainerType) spinner.getSelectedItem()).id;
                    if (localContainer != null) {
                        DBUtil.updateContainer(getContext(), localContainer.getId(), name, typeId);
                    } else {
                        Uri uri = DBUtil.addContainer(getContext(), name, typeId);
                        long id = ContentUris.parseId(uri);
                        localContainer = new Container((int) id);
                    }

                    DBUtil.deleteRelations(getContext(), relationsToBeDeleted);
                    DBUtil.addRelations(getContext(), localContainer.getId(), tempRelations);

                    postSuccess.run();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.error_name), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Open dialog to link container to data source.
        findViewById(R.id.config_add_data_source).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddDSCRelationDialog(getContext(), new Runnable() {
                    @Override
                    public void run() {
                        updateRelationList();
                    }
                }, localContainer, tempRelations).show();
            }
        });
    }

    private void updateRelationList() {
        relationAdapter.notifyDataSetChanged();
    }

    // Container type spinner adapter
    class ContainerAdapter extends ArrayAdapter {

        private EnumContainerType[] values;
        private LayoutInflater inflater;

        public ContainerAdapter(Context context) {
            super(context, R.layout.spinner_primary, R.id.text);

            values = EnumContainerType.values();

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_primary, null);
            }

            EnumContainerType item = (EnumContainerType) getItem(position);
            ((TextView) view.findViewById(R.id.text)).setText(BasicUtils.getStringResourceByName(getContext(), item.name));

            return view;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.spinner_base_dropdown, null);
            }

            EnumContainerType item = (EnumContainerType) getItem(position);
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

    // Relations list adapter
    class RelationAdapter extends ArrayAdapter {

        private List<DataSourceContainerRelation> values;
        private LayoutInflater inflater;

        public RelationAdapter(Context context) {
            super(context, R.layout.spinner_primary, R.id.text);
            inflater = LayoutInflater.from(context);

            values = new ArrayList<DataSourceContainerRelation>();

            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            values.clear();
            values.addAll(relations);
            values.addAll(tempRelations);
            values.removeAll(relationsToBeDeleted);

            Collections.sort(values, new SortValues());

            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = inflater.inflate(R.layout.element_data_source, null);
            }

            final DataSourceContainerRelation relation = (DataSourceContainerRelation) getItem(position);

            ((TextView) view.findViewById(R.id.element_text1)).setText(relation.getDatasource().getIpAddress());
            ((TextView) view.findViewById(R.id.element_text2)).setText(Integer.toString(relation.getAntenna()));
            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tempRelations.contains(relation)) {
                        tempRelations.remove(relation);
                    } else {
                        relationsToBeDeleted.add(relation);
                    }
                    notifyDataSetChanged();
                }
            });

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

        /**
         * Sort DataSourceContainerRelation by antenna
         */
        class SortValues implements Comparator<DataSourceContainerRelation> {

            @Override
            public int compare(DataSourceContainerRelation rc1, DataSourceContainerRelation rc2) {
                int i = rc1.getDatasource().getIpAddress().compareTo(rc2.getDatasource().getIpAddress());
                if (i != 0) return i;
                return rc1.getAntenna().compareTo(rc2.getAntenna());
            }
        }

    }
}
