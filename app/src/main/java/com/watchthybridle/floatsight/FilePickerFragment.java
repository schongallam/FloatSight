package com.watchthybridle.floatsight;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FilePickerFragment extends Fragment implements FileAdapter.FileItemClickListener {
    FileAdapter fileAdapter;

    public FilePickerFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.file_list_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        fileAdapter = new FileAdapter(getFiles());
        fileAdapter.setFileItemClickListener(this);
        recyclerView.setAdapter(fileAdapter);
    }

    public void onItemClick(FileAdapterItem fileAdapterItem) {
        if(fileAdapterItem.isEnabled) {
            Uri uri = Uri.fromFile(fileAdapterItem.file);
            ((MainActivity) getActivity()).loadFlySightTrackData(uri);
            getFragmentManager().popBackStackImmediate();
        }
    }

    private List<FileAdapterItem> getFiles() {
        List<FileAdapterItem> files = new ArrayList<>();
        try {
            File tracksFolder = ((MainActivity) getActivity()).getTracksFolder();
            for(File file : tracksFolder.listFiles()) {
                files.add(new FileAdapterItem(file));
            }
        } catch (FileNotFoundException e) {
            showErrorMessage(R.string.error_opening_local_storage);
        }
        return files;
    }

    protected void showErrorMessage(@StringRes int stringResId) {
        Context context = getContext();
        if(context != null) {
            new AlertDialog.Builder(context)
                    .setMessage(stringResId)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
    }
}

