package bsoft.com.musiceditor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.SelectSongAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

public class SelectFileMergerFragment extends BaseFragment implements SelectSongAdapter.OnClick {
    private Toolbar toolbar;
    private RecyclerView rvSong;
    private List<AudioEntity> audioEntityList = new ArrayList<>();
    private List<AudioEntity> listChecked = new ArrayList<>();
    private SearchView searchView;
    private SelectSongAdapter adapter;

    public static SelectFileMergerFragment newInstance() {
        Bundle args = new Bundle();
        SelectFileMergerFragment fragment = new SelectFileMergerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {

        if (searchView != null) {
            searchView.clearFocus();
        }

        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_select_folder, container, false);
        return view;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_file));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view -> getFragmentManager().popBackStack());
        toolbar.inflateMenu(R.menu.menu_select_multi_song);
        toolbar.findViewById(R.id.item_done).setOnClickListener(v -> getList());
        searchAudio();
    }

    private void getList() {

        if (listChecked.size() == 0) {

            Toast.makeText(getContext(), getString(R.string.please_choose_file), Toast.LENGTH_SHORT).show();

        } else {

            Bundle bundle = new Bundle();

            bundle.putParcelableArrayList(Keys.LIST_SONG, (ArrayList<? extends Parcelable>) listChecked);

            getFragmentManager().beginTransaction()
                    .add(R.id.view_container, SortFragment.newInstance(bundle))
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void initViews() {

        initToolbar();

        audioEntityList = Utils.getSongFromDevice(getContext());

        adapter = new SelectSongAdapter(audioEntityList, getContext(), this);

        rvSong = (RecyclerView) findViewById(R.id.rv_audio);
        rvSong.setHasFixedSize(true);
        rvSong.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSong.setAdapter(adapter);

    }

    private void searchAudio() {
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.item_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                actionSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                actionSearch(s);
                return true;
            }
        });
    }

    private void actionSearch(String s) {
        List songList = Utils.filterSong(audioEntityList, s);
        adapter.setFilter(songList);
    }

    @Override
    public void onClick(int index) {

    }

    @Override
    public void onListChecked(List<AudioEntity> listChecked) {
        this.listChecked.clear();
        this.listChecked.addAll(listChecked);
    }
}
