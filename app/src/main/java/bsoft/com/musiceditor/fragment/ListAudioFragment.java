package bsoft.com.musiceditor.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.bsoft.ringdroid.RingdroidEditActivity;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ListAudioFragment extends BaseFragment implements AudioAdapter.OnClick {
    private static final int AUDIO_CUTTER = 0;
    private RecyclerView rvAudio;
    private AudioAdapter adapter;
    private List<AudioEntity> audioEntityList = new ArrayList<>();
    private List<AudioEntity> listAllAudioEntity = new ArrayList<>();
    private SearchView searchView;
    private ProgressDialog dialogLoading;
    private int CHECK_FRAGMENT = 0;

    public static ListAudioFragment newInstance(Bundle bundle) {
        ListAudioFragment fragment = new ListAudioFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {

        if (searchView != null) {
            searchView.clearFocus();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_audio, container, false);
    }


    @Override
    public void initViews() {

        CHECK_FRAGMENT = getArguments().getInt(Keys.TITLE, 0);

        audioEntityList = new ArrayList<>();
        audioEntityList.clear();
        adapter = new AudioAdapter(audioEntityList, getContext(), this, false);

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

        if (CHECK_FRAGMENT == AUDIO_CUTTER) {
            toolbar.setTitle(getString(R.string.audio_cutter));
        } else {
            toolbar.setTitle(getString(R.string.converter));
        }

        searchAudio(toolbar);
    }

    @Override
    public void onClick(int index) {
        AudioEntity audioEntity = audioEntityList.get(index);

        if (CHECK_FRAGMENT == AUDIO_CUTTER) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_EDIT);
            intent.putExtra(Keys.FILE_NAME, audioEntity.getPath());
            intent.setClassName(getContext().getPackageName(), RingdroidEditActivity.class.getName());

            startActivityForResult(intent, 2);

        } else {

            Bundle bundle = new Bundle();
            bundle.putParcelable(Utils.AUDIO_ENTITY, audioEntity);

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.animation_left_to_right
                            , R.anim.animation_right_to_left
                            , R.anim.animation_left_to_right
                            , R.anim.animation_right_to_left)
                    .add(R.id.view_container, ConvertFragment.newInstance(bundle))
                    .addToBackStack(null)
                    .commit();

        }
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
        dialogLoading = ProgressDialog.show(getContext(), "", getString(R.string.loading), true);
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
    public boolean onLongClick(int index) {
        return false;
    }

    @Override
    public void onOptionClick(int index) {

    }
}
