package bsoft.com.musiceditor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bsoft.com.musiceditor.fragment.StudioConverterFragment;
import bsoft.com.musiceditor.fragment.StudioCutterFragment;
import bsoft.com.musiceditor.fragment.StudioMergerFragment;
import bsoft.com.musiceditor.fragment.StudioRecorderFragment;

public class StudioAdapter extends FragmentStatePagerAdapter {

    private static final String CUTTER = "Cutter";
    private static final String MERGER = "Merger";
    private static final String CONVERTER = "Converter";
    private static final String RECORDER = "Recorder";
    private static final int INDEX_CUTTER = 0;
    private static final int INDEX_MERGER = 1;
    private static final int INDEX_CONVERTER = 2;
    private static final int INDEX_RECORDER = 3;

    private String[] listTab = new String[]{CUTTER, MERGER, CONVERTER, RECORDER};

    public StudioAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INDEX_CUTTER:
                return StudioCutterFragment.newInstance();

            case INDEX_MERGER:
                return StudioMergerFragment.newInstance();

            case INDEX_CONVERTER:
                return StudioConverterFragment.newInstance();

            case INDEX_RECORDER:
                return StudioRecorderFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return listTab.length;
    }
}
