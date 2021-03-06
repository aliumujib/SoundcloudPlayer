package com.alium.soundcloudplayer.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jean.podplayer.CollapsedPlayerView;
import com.example.jean.podplayer.JcAudio;
import com.example.jean.podplayer.JcPlayerView;
import com.example.jean.podplayer.JcStatus;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import com.alium.soundcloudplayer.R;
import com.alium.soundcloudplayer.data.models.Track;
import com.alium.soundcloudplayer.data.models.User;
import com.alium.soundcloudplayer.managers.DataManager;
import com.alium.soundcloudplayer.ui.adapters.TrackListsPagerAdapter;
import com.alium.soundcloudplayer.ui.mvp.IPodcastPlayerContracts;
import com.alium.soundcloudplayer.ui.mvp.PodcastPlayerPresenter;


public class PodcastsLibraryFragment extends Fragment implements IPodcastPlayerContracts.PodcastPlayerView,
        JcPlayerView.OnInvalidPathListener,
        JcPlayerView.JcPlayerViewStatusListener,
        SlidingUpPanelLayout.PanelSlideListener {

    IPodcastPlayerContracts.PodcastPlayerPresenter mPodcastPlayerPresenter;


    private JcPlayerView mPlayer;
    private List<JcAudio> mJcAudios;
    private ViewPager mViewPager;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private CollapsedPlayerView mSmallControls;
    private String TAG = getClass().getSimpleName();

    public PodcastsLibraryFragment() {
        // Required empty public constructor
    }

    public static PodcastsLibraryFragment newInstance() {
        PodcastsLibraryFragment fragment = new PodcastsLibraryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mPodcastPlayerPresenter = new PodcastPlayerPresenter(this);
        mPlayer = (JcPlayerView) view.findViewById(R.id.jcplayer);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.addPanelSlideListener(this);

        mSmallControls = (CollapsedPlayerView) view.findViewById(R.id.collapsed_view);

        mJcAudios = new ArrayList<>();

        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        // Set users for fragments in view pager
        List<User> users = new ArrayList<>();
        users.add(new User(getString(R.string.podcasts), DataManager.MKAN_NG));
        users.add(new User(getString(R.string.playlists), DataManager.VOICE_OF_ISLAM));
        TrackListsPagerAdapter trackListsPagerAdapter = new TrackListsPagerAdapter(getChildFragmentManager(), users);

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(trackListsPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        mPlayer.initPlaylist(mJcAudios);
        mPlayer.registerInvalidPathListener(this);
        mPlayer.registerStatusListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podcasts_library, container, false);
    }


    @Override
    public IPodcastPlayerContracts.PodcastPlayerPresenter getPresenter() {
        return mPodcastPlayerPresenter;
    }

    @Override
    public void playPodCast(int position, Track track) {
        JcAudio jcAudio = JcAudio.createFromURL(track.getTitle(), track.getStreamUrl(), track.getImageUrl(), track.getOwner());
        mSmallControls.setCurrentTrack(jcAudio);
        mPlayer.playAudio(jcAudio);
    }

    @Override
    public void onPathError(JcAudio jcAudio) {
        Toast.makeText(getContext(), jcAudio.getPath() + " with problems", Toast.LENGTH_LONG).show();
        mPlayer.removeAudio(jcAudio);
        mPlayer.next();
    }


    @Override
    public void onPausedStatus(JcStatus jcStatus) {
        mPlayer.createNotification();
    }

    @Override
    public void onContinueAudioStatus(JcStatus jcStatus) {
        mSmallControls.setCurrentTrack(jcStatus.getJcAudio());
        mPlayer.createNotification();
    }

    @Override
    public void onPlayingStatus(JcStatus jcStatus) {
        mSmallControls.setCurrentTrack(jcStatus.getJcAudio());
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        mPlayer.createNotification();
    }

    @Override
    public void onTimeChangedStatus(final JcStatus jcStatus) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // calculate progress
                float progress = (float) (jcStatus.getDuration() - jcStatus.getCurrentPosition())
                        / (float) jcStatus.getDuration();
                int percent = (int) (100 - (100 * progress));
                //Log.d(TAG, String.valueOf(percent));
                mSmallControls.updateProgress(jcStatus.getJcAudio(), percent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    if (mSlidingUpPanelLayout != null &&
                            (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                                    mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onCompletedAudioStatus(JcStatus jcStatus) {

    }

    @Override
    public void onPreparedAudioStatus(JcStatus jcStatus) {

    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        mSmallControls.animate().alpha(1.0f - slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }
}
