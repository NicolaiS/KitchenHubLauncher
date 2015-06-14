package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.kaist.resl.kitchenhublauncher.Constants;
import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.fragments.ColorsFragment;
import kr.kaist.resl.kitchenhublauncher.fragments.ConfigFragment;
import kr.kaist.resl.kitchenhublauncher.fragments.InfoFragment;

/**
 * Created by nicolais on 15. 3. 25.
 * <p/>
 * Settings dialog
 */
public class SettingsDialog extends DialogFragment {

    // Views
    private ViewPager viewPager;
    private TextView tabInfo;
    private TextView tabConfig;
    private TextView tabColors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_settings, container);

        tabInfo = (TextView) view.findViewById(R.id.tab_settings_info);
        tabConfig = (TextView) view.findViewById(R.id.tab_settings_config);
        tabColors = (TextView) view.findViewById(R.id.tab_settings_colors);

        List fragments = getFragments();
        fragmentAdapter adapter = new fragmentAdapter(getChildFragmentManager(), fragments);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        // Go to colors/themes tab
        boolean goToColors = getArguments().getBoolean(Constants.BROADCAST_THEME_CHANGED, false);
        if (goToColors) viewPager.setCurrentItem(2, false);

        setupTabs();
        updateTabs();

        return view;
    }

    /**
     * Load fragments
     *
     * @return list of fragments
     */
    private List getFragments() {
        List fragments = new ArrayList();
        fragments.add(InfoFragment.newInstance());
        fragments.add(ConfigFragment.newInstance());
        fragments.add(ColorsFragment.newInstance());
        return fragments;
    }

    /**
     * Initialize tabs
     */
    private void setupTabs() {
        tabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0, false);
                updateTabs();
            }
        });

        tabConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1, false);
                updateTabs();
            }
        });

        tabColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2, false);
                updateTabs();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                updateTabs();
            }
        });
    }

    /**
     * Configure tabs layout depending on selected tab
     */
    private void updateTabs() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.color_base_true, typedValue, true);
        int baseColor = typedValue.data;
        theme.resolveAttribute(R.attr.color_primary, typedValue, true);
        int primaryColor = typedValue.data;

        switch (viewPager.getCurrentItem()) {
            case 0:
                tabInfo.setBackgroundColor(primaryColor);
                tabInfo.setTextColor(baseColor);
                tabConfig.setBackgroundResource(R.drawable.shadow_black10_in_down_left);
                tabConfig.setTextColor(primaryColor);
                tabColors.setBackgroundResource(R.drawable.shadow_black10_in_down);
                tabColors.setTextColor(primaryColor);
                break;
            case 1:
                tabInfo.setBackgroundResource(R.drawable.shadow_black10_in_down_right);
                tabInfo.setTextColor(primaryColor);
                tabConfig.setBackgroundColor(primaryColor);
                tabConfig.setTextColor(baseColor);
                tabColors.setBackgroundResource(R.drawable.shadow_black10_in_down_left);
                tabColors.setTextColor(primaryColor);
                break;
            case 2:
                tabInfo.setBackgroundResource(R.drawable.shadow_black10_in_down);
                tabInfo.setTextColor(primaryColor);
                tabConfig.setBackgroundResource(R.drawable.shadow_black10_in_down_right);
                tabConfig.setTextColor(primaryColor);
                tabColors.setBackgroundColor(primaryColor);
                tabColors.setTextColor(baseColor);
        }
    }

    class fragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public fragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
