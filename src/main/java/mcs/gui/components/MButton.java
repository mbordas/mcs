package mcs.gui.components;

import mcs.graphics.DPI;

import javax.swing.*;
import java.awt.*;

public class MButton extends JButton {

    public static final int BUTTON_MARGIN_LEFT = 30;

    public static final Color COLOR_TEXT = Color.darkGray;
    public static final Color COLOR_BACKGROUND = Color.lightGray;
    public static final Color COLOR_BACKGROUND_HOVER = Color.white;

    public MButton(String text) {
        super(text);
        DPI.adaptFontSize(this);
        setMargin(new Insets(DPI.toScale(10), DPI.toScale(BUTTON_MARGIN_LEFT), DPI.toScale(10), DPI.toScale(30)));
        setBackground(COLOR_BACKGROUND);
        setForeground(COLOR_TEXT);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(COLOR_BACKGROUND_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(COLOR_BACKGROUND);
            }
        });
    }
}
