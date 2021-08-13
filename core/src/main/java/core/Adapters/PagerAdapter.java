package core.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context context;
    private ArrayList<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragments, String[] titles) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.tabTitles = titles;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles!=null?tabTitles[position]:null;
//        return null;
    }
}