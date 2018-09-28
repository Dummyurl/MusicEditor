package bsoft.com.musiceditor.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.fragment.ConvertFragment;
import bsoft.com.musiceditor.fragment.ListAudioFragment;
import bsoft.com.musiceditor.fragment.RecordFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initToolbar();
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_ads);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.getMenu().findItem(R.id.item_ads).setOnMenuItemClickListener(v -> moreApp());
    }

    private boolean moreApp() {
        return true;
    }


    private void initView() {
        findViewById(R.id.view_cutter).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_studio).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_more_app).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_converter).setOnClickListener(v -> addFragmentConvert());
        findViewById(R.id.view_recorder).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_merger).setOnClickListener(v -> addFragmentCutter());
    }

    private void addFragmentCutter() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.view_container, RecordFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void addFragmentConvert() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.view_container, ListAudioFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }
}
