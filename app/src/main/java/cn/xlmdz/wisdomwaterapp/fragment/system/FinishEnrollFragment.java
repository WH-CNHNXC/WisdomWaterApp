package cn.xlmdz.wisdomwaterapp.fragment.system;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import cn.xlmdz.wisdomwaterapp.R;

public class FinishEnrollFragment extends Fragment {

    public FinishEnrollFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        }, 3000); //3s自动返回首页
        return inflater.inflate(R.layout.fragment_finish_enroll, container, false);
    }
}
