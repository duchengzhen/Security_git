package com.calvin.security.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.calvin.security.R;

/**
 * Created by calvin on 2014/6/19.
 */
public class ResultFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.result_scanner_fragment, container, false);
        //TODO 设置处理逻辑
        Button btnBack= (Button) rootView.findViewById(R.id.btn_result_back);


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
