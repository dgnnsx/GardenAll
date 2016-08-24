package br.com.gardenall.fragment;

/**
 * Created by diego on 23/08/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.gardenall.R;

public class FragmentPlaceHolder extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_place_holder, container, false);
        return view;
    }
}