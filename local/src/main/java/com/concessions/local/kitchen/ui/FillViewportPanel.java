package com.concessions.local.kitchen.ui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class FillViewportPanel extends JPanel implements Scrollable {

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 160;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Don't track viewport width, which allows horizontal scrolling.
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // Track the viewport height, which forces this panel to fill the viewport vertically.
        return true;
    }
}
