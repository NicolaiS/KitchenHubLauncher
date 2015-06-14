package kr.kaist.resl.kitchenhublauncher.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import kr.kaist.resl.kitchenhublauncher.Constants;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.enums.enum_themes;

/**
 * Created by nicolais on 15. 3. 25.
 * <p/>
 * Themes fragment/tab
 */
public class ColorsFragment extends Fragment {

    private GridView gridView = null;
    private ThemeAdapter adapter = null;

    public static final ColorsFragment newInstance() {
        return new ColorsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_colors, null);

        adapter = new ThemeAdapter(getActivity());

        gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        // Change theme onClick
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                enum_themes theme = (enum_themes) adapter.getItem(pos);
                changeTheme(theme);
            }
        });

        return v;
    }

    public void changeTheme(enum_themes theme) {
        SharedPreferences sPref = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, getActivity().MODE_PRIVATE);
        sPref.edit().putInt(Constants.PREF_THEME_RESID_ID, theme.getThemeId()).commit();

        Intent intent = new Intent(Constants.BROADCAST_THEME_CHANGED);
        getActivity().sendBroadcast(intent);
    }

    public class ThemeAdapter extends BaseAdapter {
        private Context mContext;

        public ThemeAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return enum_themes.values().length;
        }

        public Object getItem(int pos) {
            return enum_themes.values()[pos];
        }

        public long getItemId(int pos) {
            return pos;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int pos, View convertView, ViewGroup parent) {
            enum_themes theme = (enum_themes) getItem(pos);

            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(theme.getThumbnailId());
            return imageView;
        }
    }
}
