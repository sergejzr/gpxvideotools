package de.zerr;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Locale;

import javax.imageio.ImageTranscoder;
import javax.imageio.spi.ImageTranscoderSpi;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class SVG{
	
	public static void main(String[] args) {
		try {
			 
            // Use this to Read a Local Path
            // String svgUriImputLocation = Paths.get("https://www.contradodigital.com/logo.svg").toUri().toURL().toString();
            // Read Remote Location for SVG
            String svgUriImputLocation = "https:// www.contradodigital.com/logo.svg";
            FileReader fr=new FileReader("img/bike.svg");
            TranscoderInput transcoderInput = new TranscoderInput(fr);
 fr.close();
            // Define OutputStream Location
            OutputStream outputStream = new FileOutputStream("logoAsPngFile.png");
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
 
            // Convert SVG to PNG and Save to File System
            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.transcode(transcoderInput, transcoderOutput);
 
            // Clean Up
            outputStream.flush();
            outputStream.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
}
 