package bsoft.com.musiceditor.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.io.File;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.fragment.ConvertFragment;
import bsoft.com.musiceditor.fragment.ListAudioFragment;
import bsoft.com.musiceditor.fragment.RecordFragment;
import bsoft.com.musiceditor.fragment.StudioConverterFragment;
import bsoft.com.musiceditor.fragment.StudioFragment;
import bsoft.com.musiceditor.utils.Keys;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initToolbar();
        initFolder();
    }

    private void initFolder() {
        String dirApp, dirConverter, dirMerger, dirCutter, dirRecorder;
        dirApp = Environment.getExternalStorageDirectory().getAbsolutePath() + Keys.DIR_APP;
        dirConverter = dirApp + Keys.DIR_CONVERTER;
        dirCutter = dirApp + Keys.DIR_CUTTER;
        dirRecorder = dirApp + Keys.DIR_RECORDER;
        dirMerger = dirApp + Keys.DIR_MERGER;

        File fileApp = new File(dirApp);
        if (!fileApp.exists()) {
            fileApp.mkdir();
        }

        File fileConverter = new File(dirConverter);
        if (!fileConverter.exists()) {
            fileConverter.mkdir();
        }

        File fileCutter = new File(dirCutter);
        if (!fileCutter.exists()) {
            fileCutter.mkdir();
        }

        File fileRecorder = new File(dirRecorder);
        if (!fileRecorder.exists()) {
            fileRecorder.mkdir();
        }

        File fileMerger = new File(dirMerger);
        if (!fileMerger.exists()) {
            fileMerger.mkdir();
        }
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
        findViewById(R.id.view_studio).setOnClickListener(v -> addFragmentStudio());
        findViewById(R.id.view_more_app).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_converter).setOnClickListener(v -> addFragmentConvert());
        findViewById(R.id.view_recorder).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_merger).setOnClickListener(v -> addFragmentCutter());
    }

    private void addFragmentStudio() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.view_container, StudioFragment.newInstance())
                .addToBackStack(null)
                .commit();
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
