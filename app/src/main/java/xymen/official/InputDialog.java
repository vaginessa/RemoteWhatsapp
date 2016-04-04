package xymen.official;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class InputDialog extends DialogFragment {
    EditText ET;
    Communicator com;
    int position;

    static InputDialog newInstance(int position){
        InputDialog f = new InputDialog();
        Bundle args = new Bundle();
        args.putInt("position",position);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

//    public InputDialog(int position) {
//        Log.i("InputDialog", "Position passed is " + position);
//        this.position = position;
//        ;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        com = (Communicator) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename Client " + (position+1) + "?");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_rename_client,null);
        builder.setView(v);
        ET = (EditText) v.findViewById(R.id.edittext);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                com.sendString("false", position);
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String rename_client = ET.getText().toString();
                com.sendString(rename_client, position);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }
/*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_rename_client, null);
        ok = (Button) view.findViewById(R.id.ok);
        cancel = (Button) view.findViewById(R.id.cancel);
        ET = (EditText) view.findViewById(R.id.edittext);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setCancelable(false);
        return view;



    }*/
/*
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok) {
            String rename_client = ET.getText().toString();
            com.sendString(rename_client, position);
            dismiss();
        } else {
            com.sendString("false", position);
            dismiss();
        }

    }*/

    interface Communicator {
        void sendString(String message, int position);

    }
}
