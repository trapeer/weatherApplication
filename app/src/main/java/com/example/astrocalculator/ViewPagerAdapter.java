package com.example.astrocalculator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new Sun();
            case 1:
                return new Moon();
            case 2:
                return new Weather1();
            case 3:
                return new Weather2();
            case 4:
                return new Weather3();
        }
        return null; //does not happen
    }

    @Override
    public CharSequence  getPageTitle(int position){
        switch(position)
        {
            case 0:
                return "sun";
            case 1:
                return "moon";
            case 2:
                return "current";
            case 3:
                return "tomorrow";
            case 4:
                return "next day";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}