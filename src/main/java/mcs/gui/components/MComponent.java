package mcs.gui.components;

import mcs.graphics.DPI;
import mcs.graphics.MGraphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Catches some calls to {@link JComponent} in order to adapt DPI scale.
 */
public abstract class MComponent extends JComponent {

	@Override
	public void setSize(int width_px, int height_px) {
		super.setSize(DPI.toScale(width_px), DPI.toScale(height_px));
	}

	public void setPreferredSize(int width_px, int height_px) {
		setPreferredSize(new Dimension(width_px, height_px));
	}

	@Override
	public void setPreferredSize(Dimension dimension) {
		super.setPreferredSize(new Dimension(DPI.toScale(dimension.getWidth()), DPI.toScale(dimension.getHeight())));
	}

	@Override
	public Dimension getSize() {
		Dimension scaledSize = super.getSize();
		return new Dimension(DPI.unScale(scaledSize.width), DPI.unScale(scaledSize.height));
	}

	protected void updateDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}

	protected abstract void paintComponent(MGraphics graphics);

	@Override
	protected final void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		MGraphics mgraphics = new MGraphics(graphics);
		paintComponent(mgraphics);
	}

	public void exportJPG(File file) {
		BufferedImage awtImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		MGraphics graphics = new MGraphics(awtImage.getGraphics());
		paintComponent(graphics);

		try {
			ImageIO.write(awtImage, "jpg", file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
