/*
 * Decompiled with CFR 0.152.
 */
package ui;

import com.formdev.flatlaf.FlatLightLaf;
import data.CsvReader;
import data.DataLoader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ui.EmpresaPanel;
import ui.Local1Panel;
import ui.Local2Panel;
import ui.Local3Panel;
import ui.Local4Panel;
import ui.RutaPanel;
import ui.TitularPanel;

public class MainFrame {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("defaultFont", new Font("Arial", 0, 14));
        }
        catch (UnsupportedLookAndFeelException var19) {
            System.err.println("Error al configurar FlatLaf: " + var19.getMessage());
        }
        String directorioTrabajo = MainFrame.obtenerDirectorioRecursos();
        MainFrame.verificarRecursos(directorioTrabajo);
        JFrame frame = new JFrame("LEGRITE");
        frame.setDefaultCloseOperation(3);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        URL iconURL = MainFrame.class.getClassLoader().getResource("resources/logo.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            frame.setIconImage(icon.getImage());
        }
        JTabbedPane tabbedPane = new JTabbedPane(1, 1);
        frame.add((Component)tabbedPane, "Center");
        MainFrame.enableTabReordering(tabbedPane);
        String instaladoresPath = Paths.get(directorioTrabajo, "Instaladores.csv").toString();
        String empresasPath = Paths.get(directorioTrabajo, "Empresas.csv").toString();
        String maquinasPath = Paths.get(directorioTrabajo, "maquinas").toString();
        Map<String, String[]> instaladoresData = CsvReader.readCsv(instaladoresPath);
        Map<String, String[]> empresasData = CsvReader.readCsv(empresasPath);
        Map<String, List<String>> maquinasData = DataLoader.cargarModelosDeMaquinas(maquinasPath);
        TitularPanel titularPanel = new TitularPanel();
        EmpresaPanel empresaPanel = new EmpresaPanel(instaladoresData, empresasData, directorioTrabajo);
        Local1Panel local1Panel = new Local1Panel(maquinasData, directorioTrabajo);
        Local2Panel local2Panel = new Local2Panel(maquinasData, directorioTrabajo);
        Local3Panel local3Panel = new Local3Panel(maquinasData, directorioTrabajo);
        Local4Panel local4Panel = new Local4Panel(maquinasData, directorioTrabajo);
        RutaPanel rutaPanel = new RutaPanel(titularPanel, empresaPanel, local1Panel, local2Panel, local3Panel, local4Panel, directorioTrabajo);
        JScrollPane scrollTitular = new JScrollPane(titularPanel);
        scrollTitular.getVerticalScrollBar().setUnitIncrement(16);
        JScrollPane scrollEmpresa = new JScrollPane(empresaPanel);
        scrollEmpresa.getVerticalScrollBar().setUnitIncrement(16);
        JScrollPane scrollRuta = new JScrollPane(rutaPanel);
        scrollRuta.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Titular e Instalaci\u00f3n", scrollTitular);
        tabbedPane.addTab("Empresa e Instalador", scrollEmpresa);
        tabbedPane.addTab("Local 1", local1Panel);
        tabbedPane.addTab("Local 2", local2Panel);
        tabbedPane.addTab("Local 3", local3Panel);
        tabbedPane.addTab("Local 4", local4Panel);
        tabbedPane.addTab("Rutas y Generaci\u00f3n", scrollRuta);
        tabbedPane.setIconAt(0, MainFrame.cargarIcono("resources/titular.png"));
        tabbedPane.setIconAt(1, MainFrame.cargarIcono("resources/empresa.png"));
        tabbedPane.setIconAt(6, MainFrame.cargarIcono("resources/rutas.png"));
        JMenuBar menuBar = MainFrame.crearMenu(frame);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    private static ImageIcon cargarIcono(String path) {
        URL iconURL = MainFrame.class.getClassLoader().getResource(path);
        return iconURL != null ? new ImageIcon(iconURL) : null;
    }

    private static JMenuBar crearMenu(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem maquinasItem = new JMenuItem("M\u00e1quinas");
        maquinasItem.addActionListener(e -> {
            try {
                String rutaMaquinas = MainFrame.obtenerRutaCarpetaMaquinas();
                MainFrame.abrirCarpeta(rutaMaquinas);
            }
            catch (Exception var3x) {
                JOptionPane.showMessageDialog(frame, "No se pudo abrir la carpeta de m\u00e1quinas: " + var3x.getMessage(), "Error", 0);
            }
        });
        JMenuItem instaladoresItem = new JMenuItem("Instalador y Empresa");
        instaladoresItem.addActionListener(e -> {
            try {
                String rutaResources = MainFrame.obtenerRutaCarpetaResources();
                MainFrame.abrirCarpeta(rutaResources);
            }
            catch (Exception var3x) {
                JOptionPane.showMessageDialog(frame, "No se pudo abrir la carpeta de instaladores y empresas: " + var3x.getMessage(), "Error", 0);
            }
        });
        archivoMenu.add(maquinasItem);
        archivoMenu.add(instaladoresItem);
        menuBar.add(archivoMenu);
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaDeItem = new JMenuItem("Acerca de");
        acercaDeItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "LEGRITE v0.2\nDesarrollado por CMM.", "Acerca de", 1));
        ayudaMenu.add(acercaDeItem);
        menuBar.add(ayudaMenu);
        return menuBar;
    }

    private static void abrirCarpeta(String ruta) throws IOException {
        File carpeta = new File(ruta);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            throw new IOException("La carpeta no existe: " + ruta);
        }
        Desktop desktop = Desktop.getDesktop();
        desktop.open(carpeta);
    }

    private static String obtenerRutaCarpetaMaquinas() {
        return MainFrame.obtenerDirectorioRecursos() + File.separator + "maquinas";
    }

    private static String obtenerRutaCarpetaResources() {
        return MainFrame.obtenerDirectorioRecursos();
    }

    private static String obtenerDirectorioRecursos() {
        try {
            String jarPath = MainFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarDir = new File(jarPath).getParentFile();
            return Paths.get(jarDir.getAbsolutePath(), "resources").toString();
        }
        catch (Exception var2) {
            var2.printStackTrace();
            throw new RuntimeException("No se pudo determinar la ubicaci\u00f3n del directorio de recursos.");
        }
    }

    private static void verificarRecursos(String directorioTrabajo) {
        String[] archivosRequeridos;
        for (String archivo : archivosRequeridos = new String[]{"Instaladores.csv", "Empresas.csv", "maquinas/index.txt"}) {
            File file = new File(Paths.get(directorioTrabajo, archivo).toString());
            if (file.exists()) continue;
            JOptionPane.showMessageDialog(null, "El archivo requerido \"" + archivo + "\" no se encuentra en la carpeta recursos.\nPor favor, aseg\u00farese de que el ejecutable est\u00e1 en la misma ruta que la carpeta resources.", "Error de Recursos", 0);
            System.exit(1);
        }
    }

    private static void enableTabReordering(final JTabbedPane tabbedPane) {
        JRootPane rootPane = SwingUtilities.getRootPane(tabbedPane);
        final JPanel glassPane = (JPanel)rootPane.getGlassPane();
        glassPane.setLayout(null);
        final JLabel dragLabel = new JLabel();
        dragLabel.setOpaque(true);
        dragLabel.setVisible(false);
        glassPane.add(dragLabel);
        tabbedPane.addMouseListener(new MouseAdapter(){
            private int dragIndex = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                this.dragIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (this.dragIndex != -1) {
                    String title = tabbedPane.getTitleAt(this.dragIndex);
                    Icon icon = tabbedPane.getIconAt(this.dragIndex);
                    dragLabel.setText(title);
                    dragLabel.setIcon(icon);
                    dragLabel.setBounds(e.getXOnScreen() - 10, e.getYOnScreen() - 10, 150, 30);
                    dragLabel.setBackground(UIManager.getColor("TabbedPane.selected"));
                    dragLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    dragLabel.setVisible(true);
                    glassPane.setVisible(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int targetIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (this.dragIndex != -1 && targetIndex != -1 && this.dragIndex != targetIndex) {
                    Component draggedComponent = tabbedPane.getComponentAt(this.dragIndex);
                    String draggedTitle = tabbedPane.getTitleAt(this.dragIndex);
                    Icon draggedIcon = tabbedPane.getIconAt(this.dragIndex);
                    String draggedTooltip = tabbedPane.getToolTipTextAt(this.dragIndex);
                    tabbedPane.remove(this.dragIndex);
                    tabbedPane.insertTab(draggedTitle, draggedIcon, draggedComponent, draggedTooltip, targetIndex);
                    tabbedPane.setSelectedIndex(targetIndex);
                }
                this.dragIndex = -1;
                dragLabel.setVisible(false);
                glassPane.setVisible(false);
            }
        });
        tabbedPane.addMouseMotionListener(new MouseMotionAdapter(){

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragLabel.isVisible()) {
                    Point mousePoint = e.getPoint();
                    SwingUtilities.convertPointToScreen(mousePoint, tabbedPane);
                    SwingUtilities.convertPointFromScreen(mousePoint, glassPane);
                    dragLabel.setLocation(mousePoint.x - dragLabel.getWidth() / 2, mousePoint.y - dragLabel.getHeight() / 2);
                }
            }
        });
    }
}
