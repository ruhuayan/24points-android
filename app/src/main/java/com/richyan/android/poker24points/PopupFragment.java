package com.richyan.android.poker24points;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopupFragment extends DialogFragment {
    //popupId: rule=1, about =0;
    private int mPopupID;
    public PopupFragment() {
        // Required empty public constructor
        mPopupID = 0;
    }
    public void setmPopupID(int id){
        this.mPopupID = id;
    }
    public int getmPopupID(){
        return mPopupID;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Dialog dialog;
        mPopupID = getmPopupID();
        if(mPopupID==1){
            builder.setView(inflater.inflate(R.layout.rules, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        PopupFragment.this.dismiss();
                    }
                });
            dialog = builder.create();
        }else{
            builder.setView(inflater.inflate(R.layout.about, null))
                    // Add action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            PopupFragment.this.dismiss();
                        }
                    });
            dialog = builder.create();
        }
        final ViewGroup content = (ViewGroup) dialog.findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                View inner = content.getChildAt(0);
                content.removeViewAt(0);
                ScrollView scrollView = new ScrollView(getContext());
                scrollView.addView(inner);
                content.addView(scrollView);
            }
        });
        return dialog;
    }

}
