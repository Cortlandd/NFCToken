package com.tokens.nfc.nfctokens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.File;

/**
 * Created by Kai on 1/8/2017.
 */

public class PageAdapter extends FragmentPagerAdapter {
    private int num_pages = 2;
    private Fragment pages[];

    public PageAdapter(FragmentManager fm, ArchiveManager am) {
        super(fm);
        pages = new Fragment[2];
        pages[0] = new SendFragment();
        ListingFragment lf = new ListingFragment();
        lf.setArchiveManager(am);
        pages[1] = lf;
    }

    public int getCount() {
        return num_pages;
    }

    public Fragment getItem(int position) {
        return pages[position];
    }

 }

