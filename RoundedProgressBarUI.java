import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class RoundedProgressBarUI extends javax.swing.plaf.basic.BasicProgressBarUI {
    private final Color trackColor  = new Color(245, 245, 245);
    private final Color shadowColor = new Color(0, 0, 0, 18);

    // Palette endpoints for each band
    private static final Color RED_LO    = new Color(198, 40, 40);   // dark red
    private static final Color RED_HI    = new Color(255, 120, 120); // light red
    private static final Color YEL_LO    = new Color(255, 165, 0);   // orange-ish
    private static final Color YEL_HI    = new Color(255, 255, 0);   // bright yellow
    private static final Color GRN_LO    = new Color(76, 175, 80);   // light green
    private static final Color GRN_HI    = new Color(27, 94, 32);    // deep green

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = c.getWidth(), h = c.getHeight();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = Math.min(h, 40); // smoother, more rounded edges

        // Shadow
        g2.setColor(shadowColor);
        g2.fillRoundRect(2, 2, w - 4, h - 4, arc, arc);

        // Track
        g2.setColor(trackColor);
        g2.fillRoundRect(0, 0, w - 4, h - 4, arc, arc);

        // Compute current percent and color
        int value = progressBar.getValue();
        int max   = Math.max(1, progressBar.getMaximum()); // avoid /0
        int pct   = Math.max(0, Math.min(100, (int)Math.round(value * 100.0 / max)));
        Color barColor = colorForPercent(pct);

        // Progress width
        int amountFull = getAmountFull(c.getInsets(), w - 4, h - 4);
        if (amountFull > 0) {
            // Subtle vertical gradient using the dynamic color
            GradientPaint gp = new GradientPaint(0, 0, barColor.brighter(), 0, h, barColor.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, amountFull, h - 4, arc, arc);

            // Clip to keep right edge rounded
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w - 4, h - 4, arc, arc));
            g2.fillRect(0, 0, amountFull, h - 4);
            g2.setClip(null);
        }

        // String (centered, bold)
        if (progressBar.isStringPainted()) {
            String str = progressBar.getString();
            Font bold = progressBar.getFont().deriveFont(Font.BOLD);
            FontMetrics fm = g2.getFontMetrics(bold);
            g2.setFont(bold);
            int sw = fm.stringWidth(str);
            int sh = fm.getAscent();
            int sx = (w - sw) / 2 - 2;
            int sy = (h + sh) / 2 - 4;

            // text shadow for contrast
            g2.setColor(new Color(0, 0, 0, 70));
            g2.drawString(str, sx + 1, sy + 1);
            g2.setColor(new Color(255, 255, 255, 230));
            g2.drawString(str, sx, sy);
        }

        g2.dispose();
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        // Keep visual style consistent even if indeterminate
        paintDeterminate(g, c);
    }

    // --- Helpers ---

    private static Color colorForPercent(int pct) {
        if (pct <= 40) {
            // 0–40%: reds, dark -> light
            float t = pct / 40f; // 0..1
            return lerp(RED_LO, RED_HI, t);
        } else if (pct <= 80) {
            // 41–80%: yellows, orange-ish -> bright yellow
            float t = (pct - 40) / 40f; // 0..1
            return lerp(YEL_LO, YEL_HI, t);
        } else {
            // 81–100%: greens, light -> deep
            float t = (pct - 80) / 20f; // 0..1
            return lerp(GRN_LO, GRN_HI, t);
        }
    }

    private static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int)Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bch= (int)Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bch);
    }
}