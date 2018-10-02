package bsoft.com.musiceditor.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import bsoft.com.musiceditor.fragment.StudioConverterFragment;
import bsoft.com.musiceditor.fragment.StudioCutterFragment;
import bsoft.com.musiceditor.fragment.StudioMergerFragment;
import bsoft.com.musiceditor.fragment.StudioRecorderFragment;
import bsoft.com.musiceditor.utils.Keys;

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
                Bundle bundle = new Bundle();
                bundle.putString(Keys.CHECK_STUDIO_FRAGMENT, Keys.DIR_APP + Keys.DIR_CUTTER);
                return StudioCutterFragment.newInstance(bundle);

            case INDEX_MERGER:
                Bundle b1 = new Bundle();
                b1.putString(Keys.CHECK_STUDIO_FRAGMENT, Keys.DIR_APP + Keys.DIR_MERGER);
                return StudioCutterFragment.newInstance(b1);

            case INDEX_CONVERTER:
                Bundle b2 = new Bundle();
                b2.putString(Keys.CHECK_STUDIO_FRAGMENT, Keys.DIR_APP + Keys.DIR_CONVERTER);
                return StudioCutterFragment.newInstance(b2);

            case INDEX_RECORDER:
                Bundle b3 = new Bundle();
                b3.putString(Keys.CHECK_STUDIO_FRAGMENT, Keys.DIR_APP + Keys.DIR_RECORDER);
                return StudioCutterFragment.newInstance(b3);
        }
        return null;
    }

    @Override
    public int getCount() {
        return listTab.length;
    }
}
