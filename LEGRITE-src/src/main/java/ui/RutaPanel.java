/*
 * Decompiled with CFR 0.152.
 */
package ui;

import data.PDFProcessor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ui.EmpresaPanel;
import ui.Local1Panel;
import ui.Local2Panel;
import ui.Local3Panel;
import ui.Local4Panel;
import ui.TitularPanel;

public class RutaPanel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private String inputPath;
    private String outputPath;
    private final TitularPanel titularPanel;
    private final EmpresaPanel empresaPanel;
    private final Local1Panel local1Panel;
    private final Local2Panel local2Panel;
    private final Local3Panel local3Panel;
    private final Local4Panel local4Panel;

    public RutaPanel(TitularPanel titularPanel, EmpresaPanel empresaPanel, Local1Panel local1Panel, Local2Panel local2Panel, Local3Panel local3Panel, Local4Panel local4Panel, String directorioTrabajo) {
        this.titularPanel = titularPanel;
        this.empresaPanel = empresaPanel;
        this.local1Panel = local1Panel;
        this.local2Panel = local2Panel;
        this.local3Panel = local3Panel;
        this.local4Panel = local4Panel;
        this.inputPath = directorioTrabajo;
        this.outputPath = directorioTrabajo;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Configuraci\u00f3n de Rutas y Generaci\u00f3n de PDF", 1, 2, new Font("Arial", 1, 14)));
        this.add((Component)contentPanel, "Center");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = 2;
        JLabel inputLabel = new JLabel("Seleccionar carpeta de entrada:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add((Component)inputLabel, gbc);
        JButton inputButton = new JButton("Seleccionar");
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        contentPanel.add((Component)inputButton, gbc);
        JLabel inputPathLabel = new JLabel("Carpeta actual: " + this.inputPath);
        inputPathLabel.setFont(new Font("Arial", 2, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        contentPanel.add((Component)inputPathLabel, gbc);
        inputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(1);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                this.inputPath = chooser.getSelectedFile().getAbsolutePath();
                inputPathLabel.setText("Carpeta seleccionada: " + this.inputPath);
            }
        });
        JLabel outputLabel = new JLabel("Seleccionar carpeta de salida:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPanel.add((Component)outputLabel, gbc);
        JButton outputButton = new JButton("Seleccionar");
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        contentPanel.add((Component)outputButton, gbc);
        JLabel outputPathLabel = new JLabel("Carpeta actual: " + this.outputPath);
        outputPathLabel.setFont(new Font("Arial", 2, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        contentPanel.add((Component)outputPathLabel, gbc);
        outputButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(1);
            int result = chooser.showOpenDialog(this);
            if (result == 0) {
                File selectedDir = chooser.getSelectedFile();
                String selectedPath = selectedDir.getAbsolutePath();
                if (selectedPath.endsWith("resources") || selectedPath.contains(File.separator + "resources" + File.separator)) {
                    JOptionPane.showMessageDialog(this, "No se puede seleccionar la carpeta 'resources' como salida.\nEsto sobreescribir\u00eda las plantillas del sistema.", "Carpeta no permitida", 2);
                } else {
                    this.outputPath = selectedPath;
                    outputPathLabel.setText("Carpeta seleccionada: " + this.outputPath);
                }
            }
        });
        JButton generateButton = new JButton("Guardar y Generar PDF");
        generateButton.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = 10;
        contentPanel.add((Component)generateButton, gbc);
        generateButton.addActionListener(e -> {
            if (this.inputPath != null && this.outputPath != null) {
                File inputDir = new File(this.inputPath);
                File outputDir = new File(this.outputPath);
                if (this.outputPath.endsWith("resources") || this.outputPath.contains(File.separator + "resources" + File.separator)) {
                    JOptionPane.showMessageDialog(this, "No se puede usar la carpeta 'resources' como salida.\nEsto sobreescribir\u00eda las plantillas del sistema.", "Carpeta no permitida", 0);
                    return;
                }
                if (!inputDir.exists() || !inputDir.isDirectory()) {
                    JOptionPane.showMessageDialog(this, "La carpeta de entrada no es v\u00e1lida.", "Error", 0);
                } else if (outputDir.exists() && outputDir.isDirectory()) {
                    HashMap<String, String> formData = new HashMap<String, String>();
                    formData.putAll(this.titularPanel.getFormData());
                    formData.putAll(this.empresaPanel.getFormData());
                    this.agregarDatosSiValidos(formData, this.local1Panel, "Local 1");
                    this.agregarDatosSiValidos(formData, this.local2Panel, "Local 2");
                    this.agregarDatosSiValidos(formData, this.local3Panel, "Local 3");
                    this.agregarDatosSiValidos(formData, this.local4Panel, "Local 4");
                    if (formData.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No hay datos suficientes para generar el PDF.", "Advertencia", 2);
                    } else {
                        PDFProcessor processor = new PDFProcessor();
                        try {
                            processor.procesarPDFs(this.inputPath, this.outputPath, formData);
                            JOptionPane.showMessageDialog(this, "PDFs procesados exitosamente.", "\u00c9xito", 1);
                        }
                        catch (IOException ex) {
                            JOptionPane.showMessageDialog(this, "Error al procesar los PDFs: " + ex.getMessage(), "Error", 0);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "La carpeta de salida no es v\u00e1lida.", "Error", 0);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona ambas rutas de entrada y salida.", "Error", 0);
            }
        });
    }

    private void agregarDatosSiValidos(Map<String, String> formData, JPanel panel, String nombrePanel) {
        if (panel instanceof Local1Panel) {
            Local1Panel localPanel1 = (Local1Panel)panel;
            String selectedMachine = (String)localPanel1.comboMaquinas.getSelectedItem();
            if (selectedMachine != null && !selectedMachine.trim().isEmpty()) {
                formData.putAll(localPanel1.getFormData());
                System.out.println(nombrePanel + " agregado al PDF.");
            } else {
                System.out.println(nombrePanel + " est\u00e1 vac\u00edo. No se incluir\u00e1 en el PDF.");
            }
        } else if (panel instanceof Local2Panel) {
            Local2Panel localPanel2 = (Local2Panel)panel;
            String selectedMachine = (String)localPanel2.comboMaquinas.getSelectedItem();
            if (selectedMachine != null && !selectedMachine.trim().isEmpty()) {
                formData.putAll(localPanel2.getFormData());
                System.out.println(nombrePanel + " agregado al PDF.");
            } else {
                System.out.println(nombrePanel + " est\u00e1 vac\u00edo. No se incluir\u00e1 en el PDF.");
            }
        } else if (panel instanceof Local3Panel) {
            Local3Panel localPanel3 = (Local3Panel)panel;
            String selectedMachine = (String)localPanel3.comboMaquinas.getSelectedItem();
            if (selectedMachine != null && !selectedMachine.trim().isEmpty()) {
                formData.putAll(localPanel3.getFormData());
                System.out.println(nombrePanel + " agregado al PDF.");
            } else {
                System.out.println(nombrePanel + " est\u00e1 vac\u00edo. No se incluir\u00e1 en el PDF.");
            }
        } else if (panel instanceof Local4Panel) {
            Local4Panel localPanel4 = (Local4Panel)panel;
            String selectedMachine = (String)localPanel4.comboMaquinas.getSelectedItem();
            if (selectedMachine != null && !selectedMachine.trim().isEmpty()) {
                formData.putAll(localPanel4.getFormData());
                System.out.println(nombrePanel + " agregado al PDF.");
            } else {
                System.out.println(nombrePanel + " est\u00e1 vac\u00edo. No se incluir\u00e1 en el PDF.");
            }
        }
    }
}
