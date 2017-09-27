package com.alium.soundcloudplayer.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alium.soundcloudplayer.R;
import com.alium.soundcloudplayer.data.models.Playlist;

import java.util.List;

import io.reactivex.functions.Consumer;


public class PlayListsFragment extends BaseLibraryChildFragment {


    public PlayListsFragment() {
        // Required empty public constructor
    }

    public static PlayListsFragment newInstance() {
        PlayListsFragment fragment = new PlayListsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void initData() {
        mParentFragment.getPresenter().getAllPlayLists().subscribe(new Consumer<List<Playlist>>() {
            @Override
            public void accept(List<Playlist> playlists) throws Exception {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_lists, container, false);
    }

}
