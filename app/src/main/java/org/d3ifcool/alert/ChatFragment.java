package org.d3ifcool.alert;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView mNoData;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.item_progres_bar);
        mNoData = (TextView) rootView.findViewById(R.id.infoTextView);
        mNoData.setText(R.string.chatting);

        return rootView;
    }

}
