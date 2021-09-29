package com.example.playersample;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class ViewPagerVideoAdapter extends RecyclerView.Adapter<ViewPagerVideoAdapter.VideoViewHolder> {
    private ArrayList<VideoModel> videoList;
    public SimpleExoPlayer simpleExoplayer;
    private Context context;

    public ViewPagerVideoAdapter(ArrayList<VideoModel> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_video, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerVideoAdapter.VideoViewHolder holder, int position) {
        String video = videoList.get(position).getVideoUrl();
        holder.bindVideo(video);
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        String url = videoList.get(holder.getAdapterPosition()).getVideoUrl();
        long seek = videoList.get(holder.getAdapterPosition()).getSeekValue();
        holder.initializePlayer(url, seek);
        Log.e("AttachedToWindow", holder.getAdapterPosition()+"");
    }


    class VideoViewHolder extends RecyclerView.ViewHolder{
        private PlayerView exoplayerView;

        private TextView tvVideoId;
        private ProgressBar progressBar;
        private SeekBar seekBarLeft, seekBarRight;
        private TextView current;
        private View viewForward;
        private View viewBackward;
        double current_pos, total_duration;

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(simpleExoplayer!=null){
                    try {
                        current_pos = simpleExoplayer.getCurrentPosition();
                        current.setText(timeConversion((long) current_pos));
                        seekBarLeft.setProgress((int) current_pos/2);
                        seekBarRight.setProgress((int) current_pos/2);
                        handler.postDelayed(this, 1000);
                    } catch (IllegalStateException ed){
                        ed.printStackTrace();
                    }
                }
            }
        };

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            exoplayerView = itemView.findViewById(R.id.exoplayerView);
            tvVideoId = itemView.findViewById(R.id.tvVideoId);
            progressBar = itemView.findViewById(R.id.progressBar);
            seekBarLeft = itemView.findViewById(R.id.seekLeft);
            seekBarRight = itemView.findViewById(R.id.seekRight);
            current = itemView.findViewById(R.id.current);
            viewForward = itemView.findViewById(R.id.viewForward);
            viewBackward = itemView.findViewById(R.id.viewBackward);

            seekBarLeft.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            seekBarRight.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

        }



        private void bindVideo(String video){
            tvVideoId.setText("Video "+getAdapterPosition());

            //initializePlayer(video);
            exoplayerView.setKeepScreenOn(true);
            exoplayerView.setShutterBackgroundColor(Color.TRANSPARENT);
            exoplayerView.setKeepContentOnPlayerReset(true);
            simpleExoplayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            simpleExoplayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState){
                        case Player.STATE_READY:
                            progressBar.setVisibility(View.GONE);
                            current_pos = simpleExoplayer.getCurrentPosition();
                            total_duration = simpleExoplayer.getDuration();

                            current.setText(timeConversion((long) current_pos));
                            seekBarLeft.setMax((int) total_duration/2);
                            seekBarRight.setMax((int) total_duration/2);

                            handler.postDelayed(runnable, 1000);
                            break;
                        case Player.STATE_BUFFERING:
                            progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });

            viewBackward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.removeCallbacks(runnable);
                    simpleExoplayer.seekTo(simpleExoplayer.getCurrentPosition()-1000);
                    handler.postDelayed(runnable, 1000);
                }
            });
            viewForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.removeCallbacks(runnable);
                    simpleExoplayer.seekTo(simpleExoplayer.getCurrentPosition()+2000);
                    handler.postDelayed(runnable, 1000);
                }
            });

        }


        private MediaSource buildMediaSource(Uri uri, String type) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, context.getString(R.string.app_name)),
                    new DefaultBandwidthMeter());
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            return videoSource;
        }

        public void initializePlayer(String video, long seek){
            if(simpleExoplayer.isPlaying()){
                if(getAdapterPosition()!=0){
                    videoList.get(getAdapterPosition()-1).setSeekValue(simpleExoplayer.getCurrentPosition());
                }
                releasePlayer();
                exoplayerView.setPlayer(null);
            }
            preparePlayer(video, "");
            exoplayerView.setPlayer(simpleExoplayer);
            simpleExoplayer.setPlayWhenReady(true);
            simpleExoplayer.seekTo(seek);

        }

        public void preparePlayer(String videoUrl, String type) {
            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri, type);
            simpleExoplayer.prepare(mediaSource);
        }



        public void releasePlayer() {
            simpleExoplayer.stop();
            exoplayerView.onPause();
        }

        //time conversion
        public String timeConversion(long value) {
            String songTime;
            int dur = (int) value;
            int hrs = (dur / 3600000);
            int mns = (dur / 60000) % 60000;
            int scs = dur % 60000 / 1000;

            if (hrs > 0) {
                songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
            } else {
                songTime = String.format("%02d:%02d", mns, scs);
            }
            return songTime;
        }
    }
}