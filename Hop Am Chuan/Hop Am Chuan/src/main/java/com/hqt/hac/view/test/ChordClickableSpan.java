package com.hqt.hac.view.test;

import android.content.Context;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hac_library.components.ChordSurfaceView;
import com.hqt.hac.view.R;

/**
 * Created by Quang Trung on 11/24/13.
 */
public class ChordClickableSpan extends ClickableSpan {
    public Context context;

    @Override
    public void onClick(View view) {
        // Get text

        // TODO add check if widget instanceof TextView
        TextView tv = (TextView) view;
        // TODO add check if tv.getText() instanceof Spanned
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);

        String chordName = s.subSequence(start, end).toString().replace("[", "").replace("]", "");

        final Toast toast = new Toast(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout2 = inflater.inflate(R.layout.chordsurfaceview_toast, null);

        //                  TextView text = (TextView) layout.findViewById(R.id.customToastTextView);
        //                  text.setText("This is a custom toast");
        final ChordSurfaceView chord = (ChordSurfaceView) layout2.findViewById(R.id.chordViewA);
        chord.drawChord(chordName);

        Button btnDismiss = (Button) layout2.findViewById(R.id.close);
        btnDismiss.setText("X!");
        btnDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                chord.nextPosition();
            }
        });

        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout2);
        toast.show();
    }
}
