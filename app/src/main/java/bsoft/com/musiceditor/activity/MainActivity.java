package bsoft.com.musiceditor.activity;

import android.app.NotificationManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bsoft.core.AdmobBannerHelper;
import com.bsoft.core.BUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.io.File;
import java.util.List;

import bsoft.com.musiceditor.R;
import bsoft.com.musiceditor.fragment.SelectFileMergerFragment;
import bsoft.com.musiceditor.fragment.ListAudioFragment;
import bsoft.com.musiceditor.fragment.RecorderFragment;
import bsoft.com.musiceditor.fragment.StudioFragment;
import bsoft.com.musiceditor.utils.Flog;
import bsoft.com.musiceditor.utils.Keys;

public class MainActivity extends AppCompatActivity {

    private static final int AUDIO_CUTTER = 0;
    private static final int AUDIO_CONVERTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initToolbar();
        initFolder();
        adView();
        loadNativeAdvanced();

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

    private AdView mAdView;

    private void adView() {
        FrameLayout flAdBanner = (FrameLayout) findViewById(R.id.fl_ad_banner);
        AdmobBannerHelper admobBannerHelper = new AdmobBannerHelper(this, flAdBanner)
                .setAdSize(AdSize.BANNER)
                .setAdUnitId(getString(R.string.ad_banner_id));
        admobBannerHelper.loadAd();
    }


    private void populateContentAdView(NativeContentAd nativeContentAd, NativeContentAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd, NativeAppInstallAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
        MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
        adView.setMediaView(mediaView);

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    private FrameLayout mNativeAdLayout;

    private void loadNativeAdvanced() {
        boolean loadInstallAd = System.currentTimeMillis() % 2 == 0;
        AdLoader.Builder builder = new AdLoader.Builder(getApplicationContext(), getString(R.string.lib_crs_admob_native_id));

        if (loadInstallAd) {
            builder.forAppInstallAd(nativeAppInstallAd -> {
                if (mNativeAdLayout != null) {
                    NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.ad_app_install, null);
                    populateAppInstallAdView(nativeAppInstallAd, adView);
                    mNativeAdLayout.removeAllViews();
                    mNativeAdLayout.addView(adView);
                }
            });
        } else {
            builder.forContentAd(nativeContentAd -> {
                if (mNativeAdLayout != null) {
                    NativeContentAdView adView = (NativeContentAdView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.ad_content, null);
                    populateContentAdView(nativeContentAd, adView);
                    mNativeAdLayout.removeAllViews();
                    mNativeAdLayout.addView(adView);
                }

            });
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Flog.e("ads", "xxxxxxxxxx");
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_ads);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.getMenu().findItem(R.id.item_ads).setOnMenuItemClickListener(v -> moreApp());
    }

    private boolean moreApp() {
        BUtils.showMoreAppDialog(getSupportFragmentManager());
        return true;
    }


    private void initView() {

        mNativeAdLayout = (FrameLayout) findViewById(R.id.view_ads);

        findViewById(R.id.view_studio).setOnClickListener(v -> addFragmentStudio());
        findViewById(R.id.view_more_app).setOnClickListener(v -> moreApp());
        findViewById(R.id.view_recorder).setOnClickListener(v -> addFragmentCutter());
        findViewById(R.id.view_merger).setOnClickListener(v -> addFragmentMerger());
        findViewById(R.id.view_cutter).setOnClickListener(v -> addFragmentConvert(AUDIO_CUTTER));
        findViewById(R.id.view_converter).setOnClickListener(v -> addFragmentConvert(AUDIO_CONVERTER));

    }

    private void addFragmentMerger() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left
                        , R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left)
                .add(R.id.view_container, SelectFileMergerFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void addFragmentStudio() {

        Bundle bundle = new Bundle();
        bundle.putInt(Keys.CHECK_OPEN_STUDIO, Keys.FROM_MAIN);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left
                        , R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left)
                .add(R.id.view_container, StudioFragment.newInstance(bundle))
                .addToBackStack(null)
                .commit();
    }

    private void addFragmentCutter() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left
                        , R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left)
                .add(R.id.view_container, RecorderFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void addFragmentConvert(int checkFragment) {

        Bundle bundle = new Bundle();

        bundle.putInt(Keys.TITLE, checkFragment);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left
                        , R.anim.animation_left_to_right
                        , R.anim.animation_right_to_left)
                .add(R.id.view_container, ListAudioFragment.newInstance(bundle))
                .addToBackStack(null)
                .commit();
    }
}
