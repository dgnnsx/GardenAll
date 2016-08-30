package br.com.gardenall.adapter;

/**
 * Created by diego on 23/08/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

import br.com.gardenall.fragment.AtividadesFragment;
import br.com.gardenall.fragment.FragmentPlaceHolder;
import br.com.gardenall.fragment.PlantasFragment;

public class TabsAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        if(position == 0){
            args.putString("tab", "plantas");
            Fragment f = new PlantasFragment();
            f.setArguments(args);
            return f;
        }
        else if(position == 1){
            args.putString("tab", "atividades");
            Fragment f = new AtividadesFragment();
            f.setArguments(args);
            return f;
        }

        args.putString("tab", "favoritos");
        Fragment f = new FragmentPlaceHolder();
        f.setArguments(args);
        return f;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}