package kr.kaist.resl.kitchenhublauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.kaist.resl.kitchenhublauncher.R;
import kr.kaist.resl.kitchenhublauncher.utils.DBUtil;
import models.Recall;

/**
 * Created by nicolais on 5/5/15.
 * <p/>
 * Dialog to show recall details
 */
public class RecallDetailsDialog extends Dialog {

    public RecallDetailsDialog(Context context, final Integer recallId) {
        super(context);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_recall_details);

        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // Load product names
        String[] names = DBUtil.getRecallNames(context, recallId);
        ((TextView) findViewById(R.id.company_name)).setText(names[0]);
        ((TextView) findViewById(R.id.item_name)).setText(names[1]);

        Recall r = DBUtil.getRecallNotification(getContext(), recallId);

        ((TextView) findViewById(R.id.recall_isssue_date)).setText(r.getIssueDate().toLocaleString());
        ((TextView) findViewById(R.id.recall_description)).setText(r.getDescription());
        ((TextView) findViewById(R.id.recall_danger)).setText(r.getDanger());
        ((TextView) findViewById(R.id.recall_instructions)).setText(r.getInstructions());

        findViewById(R.id.recall_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBUtil.setRecallAccepted(getContext(), recallId);
                dismiss();
            }
        });
    }

}