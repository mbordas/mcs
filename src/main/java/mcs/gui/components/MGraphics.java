package mcs.gui.components;

import java.awt.*;

public class MGraphics {

    Graphics2D m_graphics;

    public MGraphics(Graphics graphics) {
        m_graphics = (Graphics2D)graphics;
        m_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void setPaint(Color backgroundColor) {
        m_graphics.setPaint(backgroundColor);
    }

    public void fillRect(int x_px, int y_px, int width_px, int height_px) {
        m_graphics.fillRect(MComponent.toScale(x_px), MComponent.toScale(y_px),
                MComponent.toScale(width_px), MComponent.toScale(height_px));
    }
}
