// AnimatedPressButton.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimatedPressButton extends JButton {
    private float scale = 1f;         // current drawing scale
    private float targetScale = 1f;   // animation target scale
    private final Timer animTimer;    // runs the tween
    private boolean mouseOver = false;

    public AnimatedPressButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 60 FPS-ish animation loop
        animTimer = new Timer(16, e -> {
            // simple easing toward target
            scale += (targetScale - scale) * 0.25f;
            if (Math.abs(targetScale - scale) < 0.001f) {
                scale = targetScale;
                ((Timer) e.getSource()).stop(); // don't reference field before init
            }
            repaint();
        });

        // Press / release animation
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    animateTo(0.95f); // quick shrink
                }
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (!isEnabled()) return;
                // bounce back to hover/normal state
                animateTo(mouseOver ? 1.05f : 1.05f);
            }
            @Override public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                if (isEnabled()) animateTo(1.10f); // subtle hover grow
            }
            @Override public void mouseExited(MouseEvent e) {
                mouseOver = false;
                if (isEnabled()) animateTo(1.00f); // return to normal
            }
        });
    }

    private void animateTo(float s) {
        targetScale = s;
        if (!animTimer.isRunning()) animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // scale around center
        int cx = w / 2, cy = h / 2;
        g2.translate(cx, cy);
        g2.scale(scale, scale);
        g2.translate(-cx, -cy);

        // background with slight darken while pressed
        Color base = getBackground() != null ? getBackground() : UIManager.getColor("Button.background");
        float darken = Math.max(0f, 1f - scale) * 0.6f; // darker when smaller (pressed)
        Color shaded = new Color(
                Math.max(0, (int)(base.getRed()   * (1 - darken))),
                Math.max(0, (int)(base.getGreen() * (1 - darken))),
                Math.max(0, (int)(base.getBlue()  * (1 - darken)))
        );

        // disabled desaturate
        if (!isEnabled()) {
            int r = (int)(shaded.getRed()   * 0.6);
            int gC= (int)(shaded.getGreen() * 0.6);
            int b = (int)(shaded.getBlue()  * 0.6);
            shaded = new Color(r, gC, b);
        }

        g2.setColor(shaded);
        g2.fillRoundRect(0, 0, w, h, 18, 18);

        // subtle border
        g2.setColor(shaded.darker());
        g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

        // draw text centered
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText() != null ? getText() : "";
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(getForeground() != null ? getForeground() : Color.BLACK);
        g2.drawString(text, tx, ty);

        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        // comfy padding (kept during scaling)
        return new Insets(10, 12, 10, 12);
    }
}