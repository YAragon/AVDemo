package com.primary.one;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * <p>Project: AVDemo2</p>
 * <p>Description: MediaExtratorMuxerActivity：
 * <p>Copyright (c) 2018 www.duowan.com Inc. All rights reserved.</p>
 * <p>Company: YY Inc.</p>
 *
 * @author: Aragon.Wu
 * @date: 2018-10-08
 * @vserion: 1.0
 */
public class MediaExtratorMuxerActivity extends AppCompatActivity {

    private static final String TAG = "MediaExtratorMuxer";

    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();

    private MediaExtractor mediaExtractor;

    private MediaMuxer mediaMuxer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_extrator_muxer);

        mediaExtractor = new MediaExtractor();

        findViewById(R.id.extractor_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractorMedia();
            }
        });

        findViewById(R.id.muxer_audio_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muxerAudio();
            }
        });

        findViewById(R.id.muxer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muxerMedia();
            }
        });

        findViewById(R.id.combine_video_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                combineVideo();
            }
        });
    }

    private void combineVideo() {
        new Thread() {
            @Override
            public void run() {
                try {
                    MediaExtractor videoExtractor = new MediaExtractor();
                    videoExtractor.setDataSource(SDCARD_PATH + "/output_video");
                    MediaFormat videoFormat = null;
                    int videoTrackIndex = -1;
                    int videoTrackCount = videoExtractor.getTrackCount();
                    for (int i = 0; i < videoTrackCount; i++) {
                        videoFormat = videoExtractor.getTrackFormat(i);
                        String mimeType = videoFormat.getString(MediaFormat.KEY_MIME);
                        if (mimeType.startsWith("video/")) {
                            videoTrackIndex = i;
                            break;
                        }
                    }

                    MediaExtractor audioExtractor = new MediaExtractor();
                    audioExtractor.setDataSource(SDCARD_PATH + "/output_audio");
                    MediaFormat audioFormat = null;
                    int audioTrackIndex = -1;
                    int audioTrackCount = audioExtractor.getTrackCount();
                    for (int i = 0; i < audioTrackCount; i++) {
                        audioFormat = audioExtractor.getTrackFormat(i);
                        String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);
                        if (mimeType.startsWith("audio/")) {
                            audioTrackIndex = i;
                            break;
                        }
                    }

                    videoExtractor.selectTrack(videoTrackIndex);
                    audioExtractor.selectTrack(audioTrackIndex);

                    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                    MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

                    MediaMuxer mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    int writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
                    int writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
                    mediaMuxer.start();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
                    long sampleTime = 0;
                    videoExtractor.readSampleData(byteBuffer, 0);

                    if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                        videoExtractor.advance();
                    }

                    videoExtractor.readSampleData(byteBuffer, 0);
                    long secondTime = videoExtractor.getSampleTime();
                    videoExtractor.advance();
                    long thirdTime = videoExtractor.getSampleTime();
                    sampleTime = Math.abs(thirdTime - secondTime);

                    videoExtractor.unselectTrack(videoTrackIndex);
                    videoExtractor.selectTrack(videoTrackIndex);

                    while (true) {
                        int readVideoSampleSize = videoExtractor.readSampleData(byteBuffer, 0);
                        if (readVideoSampleSize < 0) {
                            break;
                        }
                        videoBufferInfo.size = readVideoSampleSize;
                        videoBufferInfo.presentationTimeUs += sampleTime;
                        videoBufferInfo.offset = 0;
                        videoBufferInfo.flags = videoExtractor.getSampleFlags();
                        mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo);
                        videoExtractor.advance();
                    }

                    while (true) {
                        int readAudioSampleSize = audioExtractor.readSampleData(byteBuffer, 0);
                        if (readAudioSampleSize < 0) {
                            break;
                        }

                        audioBufferInfo.size = readAudioSampleSize;
                        audioBufferInfo.presentationTimeUs += sampleTime;
                        audioBufferInfo.offset = 0;
                        audioBufferInfo.flags = videoExtractor.getSampleFlags();
                        mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBuffer, audioBufferInfo);
                        audioExtractor.advance();
                    }

                    Log.d(TAG, "audioBufferInfo.presentationTimeUs：" + audioBufferInfo.presentationTimeUs);
                    Log.d(TAG, "videoBufferInfo.presentationTimeUs：" + videoBufferInfo.presentationTimeUs);

                    mediaMuxer.stop();
                    mediaMuxer.release();
                    videoExtractor.release();
                    audioExtractor.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void muxerMedia() {
        new Thread() {
            @Override
            public void run() {
                mediaExtractor = new MediaExtractor();
                int videoIndex = -1;
                try {
                    mediaExtractor.setDataSource(SDCARD_PATH + "/input.mp4");
                    int trackCount = mediaExtractor.getTrackCount();
                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                        String mimeType = trackFormat.getString(MediaFormat.KEY_MIME);
                        if (mimeType.startsWith("video/")) {
                            videoIndex = i;
                        }
                    }

                    mediaExtractor.selectTrack(videoIndex);
                    MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoIndex);
                    mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output_video", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    int trackIndex = mediaMuxer.addTrack(trackFormat);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 500);
                    int frameRate = trackFormat.getInteger(MediaFormat.KEY_FRAME_RATE);

                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    mediaMuxer.start();
                    long videoSampleTime;

                    mediaExtractor.readSampleData(byteBuffer, 0);
                    //skip first I frame
                    if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                        mediaExtractor.advance();
                    }

                    mediaExtractor.readSampleData(byteBuffer, 0);
                    mediaExtractor.advance();
                    mediaExtractor.readSampleData(byteBuffer, 0);

                    mediaExtractor.unselectTrack(videoIndex);
                    mediaExtractor.selectTrack(videoIndex);
                    while (true) {
                        int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                        if (readSampleSize < 0) {
                            break;
                        }
                        mediaExtractor.advance();
                        bufferInfo.size = readSampleSize;
                        bufferInfo.offset = 0;
                        bufferInfo.flags = mediaExtractor.getSampleFlags();
                        bufferInfo.presentationTimeUs += 1000 * 1000 / frameRate;

                        mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
                    }
                    mediaMuxer.stop();
                    mediaExtractor.release();
                    mediaMuxer.release();

                    Log.e("TAG", "finish");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void muxerAudio() {
        new Thread() {
            @Override
            public void run() {
                mediaExtractor = new MediaExtractor();
                int audioIndex = -1;
                try {
                    mediaExtractor.setDataSource(SDCARD_PATH + "/input.mp4");
                    int trackCount = mediaExtractor.getTrackCount();
                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                        if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                            audioIndex = i;
                        }
                    }
                    mediaExtractor.selectTrack(audioIndex);
                    MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex);
                    mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output_audio", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    int writeAudioIndex = mediaMuxer.addTrack(trackFormat);
                    mediaMuxer.start();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    long stampTime = 0;
                    //获取帧之间的间隔时间

                    mediaExtractor.readSampleData(byteBuffer, 0);
                    if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                        mediaExtractor.advance();
                    }
                    mediaExtractor.readSampleData(byteBuffer, 0);
                    long secondTime = mediaExtractor.getSampleTime();
                    mediaExtractor.advance();
                    mediaExtractor.readSampleData(byteBuffer, 0);
                    long thirdTime = mediaExtractor.getSampleTime();
                    stampTime = Math.abs(thirdTime - secondTime);
                    Log.e(TAG, stampTime + "");


                    mediaExtractor.unselectTrack(audioIndex);
                    mediaExtractor.selectTrack(audioIndex);
                    while (true) {
                        int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                        if (readSampleSize < 0) {
                            break;
                        }
                        mediaExtractor.advance();

                        bufferInfo.size = readSampleSize;
                        bufferInfo.flags = mediaExtractor.getSampleFlags();
                        bufferInfo.offset = 0;
                        bufferInfo.presentationTimeUs += stampTime;

                        mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
                    }
                    mediaMuxer.stop();
                    mediaMuxer.release();
                    mediaExtractor.release();
                    Log.e(TAG, "finish");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void extractorMedia() {
        new Thread() {
            @Override
            public void run() {
                FileOutputStream videoFos = null;
                FileOutputStream audioFos = null;

                try {
                    File videoFile = new File(SDCARD_PATH, "output_video.mp4");
                    if (!videoFile.exists()) {
                        videoFile.createNewFile();
                    }

                    File audioFile = new File(SDCARD_PATH, "output_audio");

                    videoFos = new FileOutputStream(videoFile);
                    audioFos = new FileOutputStream(audioFile);
                    mediaExtractor.setDataSource(SDCARD_PATH + File.separator + "input.mp4");
                    int trackCount = mediaExtractor.getTrackCount();
                    int audioTrackIndex = -1;
                    int videoTrackIndex = -1;

                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                        String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                        if (mineType.startsWith("video/")) {
                            videoTrackIndex = i;
                        } else if (mineType.startsWith("audio/")) {
                            audioTrackIndex = i;
                        }
                    }

                    ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
                    mediaExtractor.selectTrack(videoTrackIndex);

                    while (true) {
                        int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                        if (readSampleCount < 0) {
                            break;
                        }

                        byte[] buffer = new byte[readSampleCount];
                        byteBuffer.get(buffer);
                        videoFos.write(buffer);
                        byteBuffer.clear();
                        mediaExtractor.advance();

                    }

                    mediaExtractor.selectTrack(audioTrackIndex);
                    while (true) {
                        int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                        if (readSampleCount < 0) {
                            break;
                        }

                        byte[] buffer = new byte[readSampleCount];
                        byteBuffer.get(buffer);
                        audioFos.write(buffer);
                        byteBuffer.clear();
                        mediaExtractor.advance();

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mediaExtractor.release();
                    try {
                        if (null != audioFos) {
                            audioFos.close();
                        }

                        if (null != videoFos) {
                            videoFos.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MediaExtratorMuxerActivity.class));
    }

}
