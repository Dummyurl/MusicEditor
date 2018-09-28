package bsoft.com.musiceditor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bsoft.com.musiceditor.fragment.ConverterStudioFragment;
import bsoft.com.musiceditor.fragment.CutterStudioFragment;
import bsoft.com.musiceditor.fragment.MergerStudioFragment;
import bsoft.com.musiceditor.fragment.RecorderStudioFragment;

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
                return CutterStudioFragment.newInstance();

            case INDEX_MERGER:
                return MergerStudioFragment.newInstance();

            case INDEX_CONVERTER:
                return ConverterStudioFragment.newInstance();

            case INDEX_RECORDER:
                return RecorderStudioFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return listTab.length;
    }
}
