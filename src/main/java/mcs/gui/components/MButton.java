package mcs.gui.components;

import javax.swing.*;
import java.awt.*;

public class MButton extends JButton {

    public static final int BUTTON_MARGIN_LEFT = 30;

    public static final Color COLOR_TEXT = Color.darkGray;
    public static final Color COLOR_BACKGROUND = Color.lightGray;
    public static final Color COLOR_BACKGROUND_HOVER = Color.white;

    public MButton(String text) {
        super(text);
        setMargin(new Insets(10,BUTTON_MARGIN_LEFT,10,30));
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
