package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;

/**
 * Created by nicolais on 4/27/15.
 *
 * Dialog to show recall warning
 */
public class RecallWarningDialog extends Dialog {

    public RecallWarningDialog(Context context, final Integer recallId) {
        super(context);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_recall_warning);

        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // Load and set product names
        String[] names = DBUtil.getRecallNames(context, recallId);
        ((TextView) findViewById(R.id.company_name)).setText(names[0]);
        ((TextView) findViewById(R.id.item_name)).setText(names[1]);

        // Initialize button to show recall details
        findViewById(R.id.recall_warn_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RecallDetailsDialog(getContext(), recallId).show();
                dismiss();
            }
        });
    }

}
