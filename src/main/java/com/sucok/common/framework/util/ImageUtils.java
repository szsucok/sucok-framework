/*
 * Copyright 2016 - 2017 suoke & Co., Ltd.
 */
package com.sucok.common.framework.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author chendx
 * @version 1.0 created at 2017年4月21日 下午3:57:13
 *
 */
public class ImageUtils {

	public static BufferedImage read(InputStream in) throws IOException {
		try {
			return ImageIO.read(in);
		} finally {
			in.close();
		}
	}

	public static BufferedImage read(String format, InputStream in) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(format);
		ImageReader reader = (ImageReader) iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		BufferedImage bi = reader.read(0, param);
		return bi;
	}

	public static BufferedImage read(String url) throws IOException {
		return ImageIO.read(new URL(url));
	}

	public static void write(RenderedImage image, String format, OutputStream output) throws IOException {
		ImageIO.write(image, format, output);
		output.close();
	}

	public static void drawImage(BufferedImage source, BufferedImage logo) throws IOException {
		Graphics2D g = source.createGraphics();
		int width = source.getWidth() / 5;
		int height = source.getHeight() / 5;
		int x = (source.getWidth() - width) / 2;
		int y = (source.getHeight() - height) / 2;
		g.drawImage(logo, x, y, width, height, null);
		g.dispose();
	}

	public static BufferedImage rotate(String format, int degrees, InputStream src) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(format);
		ImageReader reader = (ImageReader) iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(src);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		// 进行图片拉缩
		BufferedImage image = reader.read(0, param);
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage newImage = null;
		if (degrees == 0) {
			newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.rotate(0 * Math.PI / 180);
			g.drawImage(image, 0, 0, null);
		} else if (degrees == 90) {
			newImage = new BufferedImage(h, w, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.rotate(90 * Math.PI / 180);
			g.drawImage(image, 0, -h, null);
		} else if (degrees == 180) {
			newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.rotate(180 * Math.PI / 180);
			g.drawImage(image, -w, -h, null);
		} else if (degrees == 270) {
			newImage = new BufferedImage(h, w, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			g.rotate(270 * Math.PI / 180);
			g.drawImage(image, -w, 0, null);
		}
		return newImage;
	}
}