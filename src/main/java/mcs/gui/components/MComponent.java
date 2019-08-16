package mcs.gui.components;

import javax.swing.*;
import java.awt.*;

public abstract class MComponent extends JComponent {

    public static double DPI_FACTOR = 1.5;

    @Override
    public void setSize(int width_px, int height_px) {
     super.setSize(toScale(width_px), toScale(height_px));
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(new Dimension(toScale(dimension.getWidth()), toScale(dimension.getHeight())));
    }

    protected abstract void paintComponent(MGraphics graphics);

    @Override
    protected final void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        MGraphics mgraphics = new MGraphics(graphics);
        paintComponent(mgraphics);
    }

    public static int toScale(int pixels) {
        return (int)(pixels * DPI_FACTOR);
    }

    public static int toScale(double pixels) {
        return (int)(pixels * DPI_FACTOR);
    }
}
