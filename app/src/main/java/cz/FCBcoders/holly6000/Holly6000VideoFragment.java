package cz.FCBcoders.holly6000;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

public class Holly6000VideoFragment extends Fragment {

    Holly6000ViewModel holly6000ViewModel;
    private VideoView holly6000Video = null;
    private ImageView holly6000StillImage = null;
    private boolean[] currentBtnsState;

    public Holly6000VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holly6000_video, container, false);

        MainActivity myActivity = (MainActivity) getActivity();

        holly6000ViewModel = new ViewModelProvider(requireActivity()).get(Holly6000ViewModel.class);

        ImageView smallHolly6000MonitorPicture = (ImageView) getActivity().findViewById(R.id.holly6000ConsoleHolly6000Head);
        smallHolly6000MonitorPicture.setImageResource(R.drawable.holly6000_text);

        holly6000StillImage = (ImageView) view.findViewById(R.id.holly6000_still_image);
        holly6000Video = (VideoView) view.findViewById(R.id.holly6000_video);
        holly6000Video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                startPostponedEnterTransition();
            }
        });
        holly6000Video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                animateVideoToHeadTransition();

                myActivity.restoreButtonsState(currentBtnsState);
            }
        });

        playVideo();

        postponeEnterTransition();

        return view;
    }

    public void playVideo() {
        int videoToPlay = holly6000ViewModel.getVideoToPlay();
        if (videoToPlay != 0) {
            MainActivity myActivity = (MainActivity) getActivity();
            currentBtnsState = myActivity.disableAllButtons();

            holly6000StillImage.setVisibility(View.INVISIBLE);
            holly6000Video.setVisibility(View.VISIBLE);

            holly6000Video.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName()
                    + "/" + holly6000ViewModel.getVideoToPlay()));

            //holly6000ViewModel.setVideoToPlay(0);

            holly6000Video.start();
        } else {
            animateVideoToHeadTransition();
        }

    }

    public void animateVideoToHeadTransition() {
        Animation holly6000VideoToHeadFadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.holly6000_video_to_head_fade_out);
        holly6000VideoToHeadFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holly6000Video.setVisibility(View.INVISIBLE);
                Animation holly6000VideoToHeadFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.holly6000_video_to_head_fade_in);
                holly6000VideoToHeadFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holly6000StillImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                holly6000StillImage.startAnimation(holly6000VideoToHeadFadeInAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        holly6000Video.startAnimation(holly6000VideoToHeadFadeOutAnimation);
    }
}