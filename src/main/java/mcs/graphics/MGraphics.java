package mcs.graphics;

import java.awt.*;

/**
 * Catches calls to {@link Graphics2D} in order to adapt DPI scale.
 */
public class MGraphics {

	Graphics2D m_graphics;

	public MGraphics(Graphics graphics) {
		m_graphics = (Graphics2D) graphics;
		m_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void setPaint(Color backgroundColor) {
		m_graphics.setPaint(backgroundColor);
	}

	public void fillRect(int x_px, int y_px, int width_px, int height_px) {
		m_graphics.fillRect(DPI.toScale(x_px), DPI.toScale(y_px), DPI.toScale(width_px), DPI.toScale(height_px));
	}

	public void drawOval(int x_px, int y_px, int width_px, int height_px) {
		m_graphics.drawOval(DPI.toScale(x_px), DPI.toScale(y_px), DPI.toScale(width_px), DPI.toScale(height_px));
	}

	public void fillOval(int x_px, int y_px, int width_px, int height_px) {
		m_graphics.fillOval(DPI.toScale(x_px), DPI.toScale(y_px), DPI.toScale(width_px), DPI.toScale(height_px));
	}

	public Stroke getStroke() {
		return m_graphics.getStroke();
	}

	public void setStroke(Stroke stroke) {
		m_graphics.setStroke(stroke);
	}
}
