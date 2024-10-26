package com.cook.cookbook.Activity;

import static com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.utils.TimeUtilities.formatTime;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.cook.cookbook.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class CustomPlayerUiController extends AbstractYouTubePlayerListener {

    private final YouTubePlayerTracker playerTracker;
    private final Context context;
    private final YouTubePlayer youTubePlayer;
    private final YouTubePlayerView youTubePlayerView;


    private View youtubePanel;
    private SeekBar progressBar;
    TextView videoCurrentTimeTextView;
    TextView viewDurationTextView;
    ImageButton playPauseButton;
    ImageButton fullScreenButton;
    private boolean fullscreen = false;

    CustomPlayerUiController(Context context, View customPlayerUi, YouTubePlayer youTubePlayer, YouTubePlayerView youTubePlayerView) {

        this.context = context;
        this.youTubePlayer = youTubePlayer;
        this.youTubePlayerView = youTubePlayerView;

        playerTracker = new YouTubePlayerTracker();
        youTubePlayer.addListener(playerTracker);

        initViews(customPlayerUi);
    }

    private void initViews(View playerUi){
        youtubePanel = playerUi.findViewById(R.id.video_panel);
        progressBar = playerUi.findViewById(R.id.seek_bar);
        videoCurrentTimeTextView = playerUi.findViewById(R.id.current_duration);
        viewDurationTextView = playerUi.findViewById(R.id.total_duration);
        playPauseButton = playerUi.findViewById(R.id.play_button);
        fullScreenButton = playerUi.findViewById(R.id.fullscreen_button);

        playPauseButton.setOnClickListener((view) -> {
            if(playerTracker.getState() == PlayerConstants.PlayerState.PLAYING){
                youTubePlayer.pause();
                playPauseButton.setBackgroundResource(R.drawable.play_24);
            }
            else{
                youTubePlayer.play();
                playPauseButton.setBackgroundResource(R.drawable.pause_24);
            }
        });

        fullScreenButton.setOnClickListener((view) -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            ViewGroup.LayoutParams params = youTubePlayerView.getLayoutParams();

            if (fullscreen) {
                // Exit Full Screen
                if (params != null) {
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Use wrap_content when exiting fullscreen
                    youTubePlayerView.setLayoutParams(params);
                }
                fullScreenButton.setBackgroundResource(R.drawable.fullscreen_24);
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                // Restore system UI
                ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                // Enter Full Screen
                if (params != null) {
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = displayMetrics.widthPixels; // Set height to the device height
                    youTubePlayerView.setLayoutParams(params);
                }
                fullScreenButton.setBackgroundResource(R.drawable.fullscreen_exit_24);
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // Hide system UI for fullscreen
                ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }
            fullscreen = !fullscreen;
        });


        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerTracker.getVideoDuration() > 0) {
                    // Calculate the time to seek to
                    float seekTo = (progress / 100f) * playerTracker.getVideoDuration();
                    youTubePlayer.seekTo(seekTo);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Pause the video while seeking
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Resume the video after seeking
            }
        });
    }

    @Override
    public void onReady(@NonNull YouTubePlayer youTubePlayer) {

    }

    @Override
    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
        if(state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED || state == PlayerConstants.PlayerState.VIDEO_CUED)
            youtubePanel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        else if(state == PlayerConstants.PlayerState.BUFFERING)
            youtubePanel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    @Override
    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {

        if(progressBar != null && playerTracker.getVideoDuration() > 0){
            // Calculate the Progress Percentage
            int progress = (int) (((second) / playerTracker.getVideoDuration()) * 100);
            progressBar.setProgress(progress);
        }

        videoCurrentTimeTextView.setText(formatTime(second));
    }

    @Override
    public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
        progressBar.setMax(100); // Since we are dealing with percentage (0 to 100)
        viewDurationTextView.setText(formatTime(duration));
    }
}
