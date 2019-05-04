package mcs.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Set;

public class UIUtils {

	public static boolean DEBUG = false;

	public static void setDefaultSize(int size) {
		Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
		Object[] keys = keySet.toArray(new Object[keySet.size()]);

		for(Object key : keys) {

			if(key != null && key.toString().toLowerCase().contains("font")) {

				System.out.println(key);
				Font font = UIManager.getDefaults().getFont(key);
				if(font != null) {
					font = font.deriveFont((float) size);
					UIManager.put(key, font);
				}
			}
		}
	}

	/**
	 * Draws a text on graphics in the rectangle given by its corners A (xa_px,ya_px) and B (bx_px, by_px).
	 *
	 * @param graphics
	 * @param text      The text to draw.
	 * @param ax_px
	 * @param ay_px
	 * @param width_px
	 * @param height_px
	 */
	public static void drawStringCenter(Graphics2D graphics, String text, int ax_px, int ay_px, int width_px, int height_px) {
		// Computing text boundaries box
		Rectangle2D textBox = graphics.getFontMetrics().getStringBounds(text, graphics);

		// Computing inner padding
		int paddingBottom_px = (height_px - (int) textBox.getHeight()) / 2;
		int paddingLeft_px = (width_px - (int) textBox.getWidth()) / 2;

		drawString(graphics, text, ax_px, ay_px, width_px, height_px, paddingBottom_px, paddingLeft_px);
	}

	public static void drawStringRight(Graphics2D graphics, String text, int ax_px, int ay_px, int width_px, int height_px,
			int rightPadding_px) {
		// Computing text boundaries box
		Rectangle2D textBox = graphics.getFontMetrics().getStringBounds(text, graphics);

		// Computing inner padding
		int paddingBottom_px = (height_px - (int) textBox.getHeight()) / 2;
		int paddingLeft_px = width_px - (int) textBox.getWidth() - rightPadding_px;

		drawString(graphics, text, ax_px, ay_px, width_px, height_px, paddingBottom_px, paddingLeft_px);
	}

	private static void drawString(Graphics2D graphics, String text, int ax_px, int ay_px, int width_px, int height_px,
			int paddingBottom_px, int paddingLeft_px) {
		int y_px = ay_px + height_px - paddingBottom_px;
		int x_px = ax_px + paddingLeft_px;

		graphics.drawString(text, x_px, y_px);

		Color previousColor = graphics.getColor();

		if(DEBUG) {
			graphics.setColor(Color.GREEN);
			graphics.drawRect(ax_px, ay_px, width_px, height_px);
			graphics.setColor(previousColor);
		}
	}

}
