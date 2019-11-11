package mcs.graphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Catches calls to {@link Graphics2D} in order to adapt DPI scale.
 */
public class MGraphics {

	static Font DEFAULT_FONT = null;

	public static boolean DEBUG_ENABLED = false;

	Graphics2D m_graphics;

	public MGraphics(Graphics graphics) {
		m_graphics = (Graphics2D) graphics;
		m_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(DEFAULT_FONT == null) {
			Font currentFont = m_graphics.getFont();
			DEFAULT_FONT = new Font(currentFont.getFontName(), currentFont.getStyle(), currentFont.getSize());
		}
	}

	public void setPaint(Color backgroundColor) {
		m_graphics.setPaint(backgroundColor);
	}

	public void setFontSize(float size) {
		Font currentFont = m_graphics.getFont();
		Font newFont = currentFont.deriveFont(size);
		m_graphics.setFont(newFont);
	}

	public void setFont(Font font) {
		if(font != null) {
			m_graphics.setFont(font);
		} else {
			m_graphics.setFont(DEFAULT_FONT);
		}
	}

	public int getFontSize() {
		return m_graphics.getFont().getSize();
	}

	/**
	 * Draws a line from point A to point B
	 *
	 * @param xA_px
	 * @param yA_px
	 * @param xB_px
	 * @param yB_px
	 */
	public void drawLine(int xA_px, int yA_px, int xB_px, int yB_px) {
		m_graphics.drawLine(xA_px, yA_px, xB_px, yB_px);
	}

	public void drawRect(int x_px, int y_px, int width_px, int height_px) {
		m_graphics.drawRect(DPI.toScale(x_px), DPI.toScale(y_px), DPI.toScale(width_px), DPI.toScale(height_px));
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

	/**
	 * Draws a text on graphics in the rectangle given by its corners A (xa_px,ya_px) and B (bx_px, by_px).
	 *
	 * @param text      The text to draw.
	 * @param ax_px
	 * @param ay_px
	 * @param width_px
	 * @param height_px
	 */
	public void drawStringCenter(String text, int ax_px, int ay_px, int width_px, int height_px) {
		// Computing text boundaries box
		Rectangle2D textBox = m_graphics.getFontMetrics().getStringBounds(text, m_graphics);

		// Computing inner padding
		int paddingBottom_px = (height_px - (int) textBox.getHeight()) / 2;
		int paddingLeft_px = (width_px - (int) textBox.getWidth()) / 2;

		drawString(text, ax_px, ay_px, width_px, height_px, paddingBottom_px, paddingLeft_px);
	}

	public void drawStringRight(String text, int ax_px, int ay_px, int width_px, int height_px,
			int rightPadding_px) {
		// Computing text boundaries box
		Rectangle2D textBox = m_graphics.getFontMetrics().getStringBounds(text, m_graphics);

		// Computing inner padding
		int paddingBottom_px = (height_px - (int) textBox.getHeight()) / 2;
		int paddingLeft_px = width_px - (int) textBox.getWidth() - rightPadding_px;

		drawString(text, ax_px, ay_px, width_px, height_px, paddingBottom_px, paddingLeft_px);
	}

	public void drawStringLeft(String text, int ax_px, int ay_px, int width_px, int height_px,
			int leftPadding_px) {
		// Computing text boundaries box
		Rectangle2D textBox = m_graphics.getFontMetrics().getStringBounds(text, m_graphics);

		// Computing inner padding
		int paddingBottom_px = (height_px - (int) textBox.getHeight()) / 2;

		drawString(text, ax_px, ay_px, width_px, height_px, paddingBottom_px, leftPadding_px);
	}

	private void drawString(String text, int ax_px, int ay_px, int width_px, int height_px,
			int paddingBottom_px, int paddingLeft_px) {
		int y_px = ay_px + height_px - paddingBottom_px;
		int x_px = ax_px + paddingLeft_px;

		m_graphics.drawString(text, DPI.toScale(x_px), DPI.toScale(y_px));

		Color previousColor = m_graphics.getColor();

		if(DEBUG_ENABLED) {
			m_graphics.setColor(Color.GREEN);
			m_graphics.drawRect(DPI.toScale(ax_px), DPI.toScale(ay_px), DPI.toScale(width_px), DPI.toScale(height_px));
			m_graphics.setColor(previousColor);
		}
	}

	public Stroke getStroke() {
		return m_graphics.getStroke();
	}

	public void setStroke(Stroke stroke) {
		m_graphics.setStroke(stroke);
	}

	public void setStroke_px(int width_px) {
		setStroke(new BasicStroke(DPI.toScale(width_px)));
	}
}
