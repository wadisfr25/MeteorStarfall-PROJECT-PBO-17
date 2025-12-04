import java.awt.Component;
import javax.swing.*;

public class ExitHandler {

    public static boolean confirmExit(Component parent) {
        int pilih = JOptionPane.showConfirmDialog(
                parent,
                "Apakah kamu yakin ingin keluar dari program?",
                "Konfirmasi Keluar Program",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return pilih == JOptionPane.YES_OPTION;
    }
}   