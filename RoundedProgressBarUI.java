import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicProgressBarUI;

@SuppressWarnings("serial")
public class RoundedProgressBarUI extends BasicProgressBarUI {
    private static final int PAD = 2;

    private final Color trackColor  = new Color(245, 245, 245);
    private final Color shadowColor = new Color(0, 0, 0, 18);

    // Palette endpoints for each band
    private static final Color RED_LO = new Color(198, 40, 40);
    private static final Color RED_HI = new Color(255, 120, 120);
    private static final Color YEL_LO = new Color(255, 165, 0);
    private static final Color YEL_HI = new Color(255, 255, 0);
    private static final Color GRN_LO = new Color(76, 175, 80);
    private static final Color GRN_HI = new Color(27, 94, 32);

    @Override
    public Dimension getPreferredSize(JComponent c) {
        // Respect caller’s width; keep a nice default height
        Dimension d = super.getPreferredSize(c);
        if (d == null) d = new Dimension(0, 34);
        return new Dimension(d.width, Math.max(28, d.height));
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = c.getWidth();
        int h = c.getHeight();

        Insets ins = progressBar.getInsets();
        int x = ins.left + PAD;
        int y = ins.top  + PAD;
        int wBar = Math.max(0, w - ins.left - ins.right - PAD * 2);
        int hBar = Math.max(0, h - ins.top  - ins.bottom - PAD * 2);
        int arc  = Math.min(hBar, 24);

        // Shadow
        g2.setColor(shadowColor);
        g2.fillRoundRect(x + 1, y + 1, wBar, hBar, arc, arc);

        // Track
        g2.setColor(trackColor);
        RoundRectangle2D rr = new RoundRectangle2D.Float(x, y, wBar, hBar, arc, arc);
        g2.fill(rr);

        // Progress
        int value = progressBar.getValue();
        int max   = Math.max(1, progressBar.getMaximum());
        int pct   = Math.max(0, Math.min(100, (int)Math.round(value * 100.0 / max)));
        Color barColor = colorForPercent(pct);

        int amountFull = getAmountFull(ins, wBar, hBar);
        if (amountFull > 0) {
            Shape clip = g2.getClip();
            g2.setClip(rr);
            GradientPaint gp = new GradientPaint(0, y, barColor.brighter(), 0, y + hBar, barColor.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, amountFull, hBar, arc, arc);
            g2.setClip(clip);
        }

        // String
        if (progressBar.isStringPainted()) {
            paintCenteredString(g2, c, pct);
        }
        g2.dispose();
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        paintDeterminate(g, c); // same visual
    }

    // --- helpers ---
    private static Color colorForPercent(int pct) {
        if (pct <= 40) {
            float t = pct / 40f;
            return lerp(RED_LO, RED_HI, t);
        } else if (pct <= 80) {
            float t = (pct - 40) / 40f;
            return lerp(YEL_LO, YEL_HI, t);
        } else {
            float t = (pct - 80) / 20f;
            return lerp(GRN_LO, GRN_HI, t);
        }
    }

    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bch= Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bch);
    }

    private void paintCenteredString(Graphics2D g2, JComponent c, int pct) {
        String str = progressBar.getString();
        // If no custom string set, use "NN%"
        if (str == null || str.isEmpty()) str = pct + "%";

        // Choose readable color (simple luminance check)
        Color sample = colorForPercent(pct);
        double luminance = (0.2126 * sample.getRed() + 0.7152 * sample.getGreen() + 0.0722 * sample.getBlue());
        Color text = (luminance < 140) ? new Color(255, 255, 255, 230) : new Color(33, 33, 33, 230);
        Color shadow = (luminance < 140) ? new Color(0, 0, 0, 70) : new Color(255, 255, 255, 120);

        Font bold = progressBar.getFont().deriveFont(Font.BOLD);
        FontMetrics fm = g2.getFontMetrics(bold);
        g2.setFont(bold);

        int sw = fm.stringWidth(str);
        int sh = fm.getAscent();
        int sx = (c.getWidth() - sw) / 2;
        int sy = (c.getHeight() + sh) / 2 - 2;

        g2.setColor(shadow);
        g2.drawString(str, sx + 1, sy + 1);
        g2.setColor(text);
        g2.drawString(str, sx, sy);
    }
}