import javax.swing.*;
import java.awt.*;

public class Utils {
    public static void styleButton(AbstractButton b) {
        b.setBackground(Color.decode("#2b2f77"));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }
}
