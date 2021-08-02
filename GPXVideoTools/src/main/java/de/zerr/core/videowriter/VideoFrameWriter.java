package de.zerr.core.videowriter;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_NONE;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

import java.awt.image.BufferedImage;
import java.io.File;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;

@SuppressWarnings("PMD.BeanMembersShouldSerialize") // This class is not serializable
public final class VideoFrameWriter implements FrameWriter {

  
    private final Java2DFrameConverter frameConverter;
    private final FrameRecorder recorder;

    public VideoFrameWriter(final File file, final double fps, final int width, final int height) throws Exception {
        frameConverter = new Java2DFrameConverter();

        try {
            recorder = FFmpegFrameRecorder.createDefault(file, width, height);
        } catch (final FrameRecorder.Exception e) {
            throw new Exception();
        }

        recorder.setFormat("matroska"); // mp4 doesn't support streaming
        recorder.setAudioCodec(AV_CODEC_ID_NONE);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
        recorder.setFormat("mp4");
        recorder.setVideoQuality(24);
        recorder.setFrameRate(fps);

        try {
            recorder.start();
        } catch (final FrameRecorder.Exception e) {
            throw new Exception();
        }
    }

    @Override
    public void addFrame( final BufferedImage image) {
         final Frame frame = frameConverter.convert(image);
        try {
            recorder.record(frame);
        } catch (final FrameRecorder.Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void close() {
        try {
            recorder.close();
        } catch (final FrameRecorder.Exception e) {
            throw new RuntimeException();
        }
    }
}