package bsoft.com.musiceditor.fragment;

import android.app.ProgressDialog;
import android.database.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Utils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ListAudioFragment extends BaseFragment implements AudioAdapter.OnClick {
    private RecyclerView rvAudio;
    private AudioAdapter adapter;
    private List<AudioEntity> audioEntityList = new ArrayList<>();
    private List<AudioEntity> listAllAudioEntity = new ArrayList<>();
    private SearchView searchView;
    private ProgressDialog dialogLoading;

    public static ListAudioFragment newInstance() {
        Bundle args = new Bundle();
        ListAudioFragment fragment = new ListAudioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_audio, container, false);
    }

    @Override
    public void initViews() {

        audioEntityList = new ArrayList<>();
        audioEntityList.clear();
        adapter = new AudioAdapter(audioEntityList, getContext(), this);

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setAdapter(adapter);

        loadData();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.converter);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
        toolbar.inflateMenu(R.menu.menu_search);

        searchAudio(toolbar);
    }

    @Override
    public void onClick(int index) {

        AudioEntity audioEntity = audioEntityList.get(index);

        Bundle bundle = new Bundle();

        bundle.putParcelable(Utils.AUDIO_ENTITY, audioEntity);

        getFragmentManager().beginTransaction()
                .add(R.id.view_container, ConvertFragment.newInstance(bundle))
                .addToBackStack(null)
                .commit();
    }

    private void searchAudio(Toolbar toolbar) {
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


    private void loadData() {
        dialogLoading = ProgressDialog.show(requireContext(), "", getString(R.string.loading), true);
        dialogLoading.setCancelable(false);
        dialogLoading.show();

        io.reactivex.Observable observable = io.reactivex.Observable.just(Utils.getSongFromDevice(getContext()));
        observable.subscribe(new Observer<List<AudioEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AudioEntity> songs) {
                listAllAudioEntity.clear();
                listAllAudioEntity.addAll(songs);
                audioEntityList.clear();
                audioEntityList.addAll(listAllAudioEntity);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                adapter.notifyDataSetChanged();
                dialogLoading.dismiss();
            }
        });
    }

    private void actionSearch(String s) {
        audioEntityList = Utils.filterAudioEntity(listAllAudioEntity, s);
        adapter.setFilter(audioEntityList);
    }

    @Override
    public void onLongClick(int index) {

    }
}
