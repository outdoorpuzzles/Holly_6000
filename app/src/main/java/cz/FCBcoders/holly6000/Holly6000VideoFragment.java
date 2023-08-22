package cz.FCBcoders.holly6000;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

public class Holly6000VideoFragment extends Fragment {

    private VideoView holly6000Video = null;

    public Holly6000VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holly6000_video, container, false);

        holly6000Video = (VideoView) view.findViewById(R.id.holly6000_video);
        holly6000Video.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.black_bg_croped));
        holly6000Video.start();

        return view;
    }
}