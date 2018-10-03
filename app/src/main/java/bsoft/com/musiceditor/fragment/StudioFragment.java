package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.StudioAdapter;
import bsoft.com.musiceditor.utils.Keys;

public class StudioFragment extends BaseFragment {
    public static final int AUDIO_CUTTER = 0;
    public static final int AUDIO_MERGER = 1;
    public static final int AUDIO_CONVERTER = 2;
    public static final int AUDIO_RECORDER = 3;
    private StudioAdapter studioAdapter;
    private ViewPager viewPager;
    public Toolbar toolbar;
    private SearchView searchView;
    private StudioConverterFragment studioConverterFragment;
    private StudioCutterFragment studioCutterFragment;
    private StudioRecorderFragment studioRecorderFragment;
    private StudioMergerFragment studioMergerFragment;

    public static StudioFragment newInstance(Bundle bundle) {
        //Bundle args = new Bundle();
        StudioFragment fragment = new StudioFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private int CHECK_STATE_ADD = 0;
    private int OPEN_FRAGMENT = 0;

    @Override
    public void initViews() {

        CHECK_STATE_ADD = getArguments().getInt(Keys.CHECK_OPEN_STUDIO);
        OPEN_FRAGMENT = getArguments().getInt(Keys.OPEN_FRAGMENT, 0);

        addTabFragment();
        initToolbar();
    }

    @Override
    public void onDestroy() {
        if (searchView == null) {
            return;
        } else {
            searchView.clearFocus();
        }
        super.onDestroy();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.studio);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
        toolbar.inflateMenu(R.menu.menu_search);
        searchAudio();
    }

    private void searchAudio() {

        MenuItem menuItem = toolbar.getMenu().findItem(R.id.item_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //actionSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                PagerAdapter pagerAdapter = viewPager.getAdapter();

                for (int i = 0; i < pagerAdapter.getCount(); i++) {

                    Fragment viewPagerFragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, i);

                    if (viewPagerFragment.isAdded()) {

                        if (viewPagerFragment instanceof StudioCutterFragment) {

                            studioCutterFragment = (StudioCutterFragment) viewPagerFragment;

                            if (studioCutterFragment != null) {
                                studioCutterFragment.beginSearch(s);
                            }
                        }
                    }
                }

                return true;
            }
        });
    }

    private void addTabFragment() {

        studioAdapter = new StudioAdapter(getChildFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(studioAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(AUDIO_CUTTER).setText(getString(R.string.cutter));
        tabLayout.getTabAt(AUDIO_MERGER).setText(getString(R.string.merger));
        tabLayout.getTabAt(AUDIO_CONVERTER).setText(getString(R.string.converter));
        tabLayout.getTabAt(AUDIO_RECORDER).setText(getString(R.string.recorder));

        if (CHECK_STATE_ADD == Keys.FROM_RECORDER) {
            viewPager.setCurrentItem(AUDIO_RECORDER);
            viewPager.setOffscreenPageLimit(3);
            return;
        }

        if (OPEN_FRAGMENT == AUDIO_MERGER) {
            viewPager.setCurrentItem(AUDIO_MERGER);

        } else if (OPEN_FRAGMENT == AUDIO_CONVERTER) {
            viewPager.setCurrentItem(AUDIO_CONVERTER);

        } else {
            viewPager.setCurrentItem(AUDIO_CUTTER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_studio, container, false);
    }
}
