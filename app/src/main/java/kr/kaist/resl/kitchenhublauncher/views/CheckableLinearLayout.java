package kr.kaist.resl.kitchenhublauncher.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.kaist.resl.kitchenhublauncher.R;

/*
 * Element class ListViews that needs to have checkable items.
 * Background will change between R.attr.color_base (unchecked) and R.attr.color_primary (checked).
 * All Textviews will change between R.attr.color_contrast_true (unchecked) and R.attr.color_base_true (checked).
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    boolean checked = false;

    List<TextView> tvs = new ArrayList<TextView>();

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int colorBase;
    private int colorBaseTrue;
    private int colorContrastTrue;
    private int colorPrimary;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i = 0; i < getChildCount(); ++i) {
            View v = getChildAt(i);
            if (v instanceof TextView) {
                tvs.add((TextView) v);
            } else if (v instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) v;
                for (int j = 0; j < ll.getChildCount(); ++j) {
                    View llc = ll.getChildAt(j);
                    if (llc instanceof TextView) {
                        tvs.add((TextView) llc);
                    }
                }
            }
        }

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.color_base, typedValue, true);
        colorBase = typedValue.data;
        theme.resolveAttribute(R.attr.color_base_true, typedValue, true);
        colorBaseTrue = typedValue.data;
        theme.resolveAttribute(R.attr.color_contrast_true, typedValue, true);
        colorContrastTrue = typedValue.data;
        theme.resolveAttribute(R.attr.color_primary, typedValue, true);
        colorPrimary = typedValue.data;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        updateViews();
    }

    @Override
    public void toggle() {
        checked = !checked;
        updateViews();
    }

    private void updateViews() {
        if (isChecked()) {
            setBackgroundColor(colorPrimary);
        } else {
            setBackgroundColor(colorBase);
        }

        for (TextView tv : tvs) {
            if (isChecked()) {
                tv.setTextColor(colorBaseTrue);
            } else {
                tv.setTextColor(colorContrastTrue);
            }
        }
    }
}