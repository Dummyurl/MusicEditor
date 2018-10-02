package bsoft.com.musiceditor.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.adapter.AudioAdapter;
import bsoft.com.musiceditor.adapter.SimpleItemTouchHelperCallback;
import bsoft.com.musiceditor.adapter.SortAdapter;
import bsoft.com.musiceditor.listener.IListSongChanged;
import bsoft.com.musiceditor.model.AudioEntity;
import bsoft.com.musiceditor.utils.Keys;
import bsoft.com.musiceditor.utils.Utils;

public class SortFragment extends BaseFragment implements IListSongChanged, SortAdapter.OnStartDragListener {
    private List<AudioEntity> audioEntities;
    private RecyclerView rvAudio;
    private SortAdapter audioAdapter;
    private Toolbar toolbar;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper itemTouchHelper;

    public static SortFragment newInstance(Bundle bundle) {
        SortFragment fragment = new SortFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews() {

        audioEntities = getArguments().getParcelableArrayList(Keys.LIST_SONG);
        audioAdapter = new SortAdapter(audioEntities, getContext(), this, this);

        rvAudio = (RecyclerView) findViewById(R.id.rv_audio);
        rvAudio.setHasFixedSize(true);
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setAdapter(audioAdapter);

        callback = new SimpleItemTouchHelperCallback(audioAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvAudio);

        initToolbar();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.sort);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
        toolbar.inflateMenu(R.menu.menu_merger);
        toolbar.getMenu().findItem(R.id.item_ads).setOnMenuItemClickListener(item -> true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sort, container, false);
    }

    @Override
    public void onNoteListChanged(List<AudioEntity> audioEntities) {

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}
