package mcs.gui.components;

import mcs.graphics.DPI;
import mcs.graphics.MGraphics;

import javax.swing.*;
import java.awt.*;

/**
 * Catches some calls to {@link JComponent} in order to adapt DPI scale.
 */
public abstract class MComponent extends JComponent {

	@Override
	public void setSize(int width_px, int height_px) {
		super.setSize(DPI.toScale(width_px), DPI.toScale(height_px));
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

	protected abstract void paintComponent(MGraphics graphics);

	@Override
	protected final void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		MGraphics mgraphics = new MGraphics(graphics);
		paintComponent(mgraphics);
	}

}
