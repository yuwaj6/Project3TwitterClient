package com.codepath.apps.simpletweets.Fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.Utils;

/**
 * Created by reneewu on 3/4/2017.
 */

public class ComposeFragment  extends DialogFragment {

    private EditText mEditText;

    private TextView tvRemainingCount;

    public ComposeFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeFragment newInstance(/*User user,*/ String preFillBody) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        //args.putParcelable("User", Parcels.wrap(user));
        args.putString("body", preFillBody);
        frag.setArguments(args);
        return frag;
    }

    public static ComposeFragment newInstance(String preFillBody, long replyToId) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("body", preFillBody);
        args.putLong("replyToId", replyToId);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.editText);
        tvRemainingCount = (TextView) view.findViewById(R.id.tvRemainingCount);

        //TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        //TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
        ImageView ivProfile = (ImageView) view.findViewById(R.id.ivProfile);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String draft = pref.getString("post_draft", "");

        mEditText.setText(draft);
        tvRemainingCount.setText(String.valueOf(140-draft.length()));

        //User user =  Parcels.unwrap(getArguments().getParcelable("User"));
        String preFillBody = getArguments().getString("body");

        if(preFillBody!=""){
            if(preFillBody.length()>140)
                preFillBody = preFillBody.substring(0,139);

            mEditText.setText(preFillBody);
            tvRemainingCount.setText(String.valueOf(140-preFillBody.length()));
        }

        final Long replyToId = getArguments().getLong("replyToId", 0);

        //tvUserName.setText(user.getName());
        //tvScreenName.setText(user.getScreenName());
        Glide.with(getContext()).load(Utils.profileImageUrl).into(ivProfile);

        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();

        //getDialog().getWindow().setSoftInputMode(
        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Button btnTweet = (Button) view.findViewById(R.id.button);

        btnTweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ComposeFragmentListner listener = (ComposeFragmentListner) getActivity();

                String body = mEditText.getText().toString();

                if(replyToId!=0)
                    listener.onFinishComposeDialog( body,replyToId );
                else
                    listener.onFinishComposeDialog( body );

                SharedPreferences pref =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("post_draft", "");
                edit.commit();

                dismiss();
            }
        });

        ImageView btnClose = (ImageView) view.findViewById(R.id.ivClose);

        btnClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mEditText.getText().length()>0)
                    onDialogClosed();
                else
                    dismiss();
            }
        });

        mEditText.addTextChangedListener(mTextEditorWatcher);
    }

    public interface ComposeFragmentListner{
        void onFinishComposeDialog(String post);
        void onFinishComposeDialog(String post, Long replyToId);
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            int remainCount = 140 - s.length();
            tvRemainingCount.setText(String.valueOf(remainCount));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void onDialogClosed(){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
        alertbox.setTitle("Message");
        alertbox.setMessage("Do you want to save post to draft?");

        alertbox.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // save
                        SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("post_draft", mEditText.getText().toString());
                        edit.commit();

                        dismiss();
                    }
                });

        alertbox.setNeutralButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("post_draft", "");
                        edit.commit();
                        dismiss();
                    }
                });

        alertbox.show();
    }
}
