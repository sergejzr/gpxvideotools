package de.zerr.gui;

public interface IAnimateController {

	public void seek(int value) throws org.bytedeco.javacv.FFmpegFrameGrabber.Exception ;

	public void fired();

}
