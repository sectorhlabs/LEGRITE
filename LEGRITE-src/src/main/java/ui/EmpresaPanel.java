/*
 * Decompiled with CFR 0.152.
 */
package ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EmpresaPanel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private static JTextField[] fieldsInstalador;
    private static JTextField[] fieldsEmpresa;
    private JComboBox<String> comboInstaladores;
    private JComboBox<String> comboEmpresas;
    private final String instaladoresCsvPath;
    private final String empresasCsvPath;

    public EmpresaPanel(Map<String, String[]> instaladoresData, Map<String, String[]> empresasData, String directorioRecursos) {
        this.instaladoresCsvPath = directorioRecursos + "/Instaladores.csv";
        this.empresasCsvPath = directorioRecursos + "/Empresas.csv";
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel labelInstalador = new JLabel("Seleccionar Instalador:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        this.add((Component)labelInstalador, gbc);
        String[] instaladoresOrdenados = new TreeSet<String>(instaladoresData.keySet()).toArray(new String[0]);
        this.comboInstaladores = new JComboBox<String>(instaladoresOrdenados);
        gbc.gridx = 1;
        this.add(this.comboInstaladores, gbc);
        JButton addInstaladorButton = new JButton("Agregar Instalador");
        gbc.gridx = 2;
        this.add((Component)addInstaladorButton, gbc);
        JLabel labelEmpresa = new JLabel("Seleccionar Empresa:");
        gbc.gridx = 0;
        ++gbc.gridy;
        this.add((Component)labelEmpresa, gbc);
        String[] empresasOrdenadas = new TreeSet<String>(empresasData.keySet()).toArray(new String[0]);
        this.comboEmpresas = new JComboBox<String>(empresasOrdenadas);
        gbc.gridx = 1;
        this.add(this.comboEmpresas, gbc);
        JButton addEmpresaButton = new JButton("Agregar Empresa");
        gbc.gridx = 2;
        this.add((Component)addEmpresaButton, gbc);
        JLabel tituloInstalador = new JLabel("DATOS DEL INSTALADOR");
        tituloInstalador.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 3;
        this.add((Component)tituloInstalador, gbc);
        String[] labelsInstalador = new String[]{"Primer apellido", "Segundo apellido", "Nombre", "NIF", "N\u00famero de carnet de instalador", "Tipo de la v\u00eda", "Nombre de la v\u00eda", "N\u00famero", "Bloque", "Portal", "Escalera", "Piso", "Puerta", "Localidad", "Provincia", "C\u00f3digo postal", "Tel\u00e9fono", "E-mail"};
        fieldsInstalador = new JTextField[labelsInstalador.length];
        for (int i = 0; i < labelsInstalador.length; ++i) {
            JLabel label = new JLabel(labelsInstalador[i]);
            gbc.gridx = 0;
            ++gbc.gridy;
            gbc.gridwidth = 1;
            this.add((Component)label, gbc);
            EmpresaPanel.fieldsInstalador[i] = new JTextField(20);
            gbc.gridx = 1;
            this.add((Component)fieldsInstalador[i], gbc);
        }
        JLabel tituloEmpresa = new JLabel("DATOS DE LA EMPRESA");
        tituloEmpresa.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 3;
        this.add((Component)tituloEmpresa, gbc);
        String[] labelsEmpresa = new String[]{"Referencia", "Primer apellido", "Segundo apellido", "Nombre o raz\u00f3n social", "CIF", "N\u00famero de Registro", "Tipo de la v\u00eda", "Nombre de la v\u00eda", "N\u00famero", "Bloque", "Portal", "Escalera", "Piso", "Puerta", "Localidad", "Provincia", "C\u00f3digo Postal", "Tel\u00e9fono", "Email"};
        fieldsEmpresa = new JTextField[labelsEmpresa.length];
        for (int i = 0; i < labelsEmpresa.length; ++i) {
            JLabel label = new JLabel(labelsEmpresa[i]);
            gbc.gridx = 0;
            ++gbc.gridy;
            gbc.gridwidth = 1;
            this.add((Component)label, gbc);
            EmpresaPanel.fieldsEmpresa[i] = new JTextField(20);
            gbc.gridx = 1;
            this.add((Component)fieldsEmpresa[i], gbc);
        }
        this.comboInstaladores.addActionListener(e -> {
            String selected = (String)this.comboInstaladores.getSelectedItem();
            if (selected != null) {
                String[] data = (String[])instaladoresData.get(selected);
                this.rellenarCampos(fieldsInstalador, data);
            }
        });
        this.comboEmpresas.addActionListener(e -> {
            String selected = (String)this.comboEmpresas.getSelectedItem();
            if (selected != null) {
                String[] data = (String[])empresasData.get(selected);
                this.rellenarCampos(fieldsEmpresa, data);
            }
        });
        addInstaladorButton.addActionListener(e -> this.agregarNuevo(instaladoresData, this.comboInstaladores, this.instaladoresCsvPath, labelsInstalador));
        addEmpresaButton.addActionListener(e -> this.agregarNuevo(empresasData, this.comboEmpresas, this.empresasCsvPath, labelsEmpresa));
    }

    private void rellenarCampos(JTextField[] fields, String[] data) {
        for (int i = 0; i < fields.length; ++i) {
            if (data != null && i < data.length) {
                fields[i].setText(data[i]);
                continue;
            }
            fields[i].setText("");
        }
    }

    private void agregarNuevo(Map<String, String[]> dataMap, JComboBox<String> comboBox, String filePath, String[] labels) {
        JTextField[] inputs = new JTextField[labels.length];
        JPanel panel = new JPanel(new GridLayout(labels.length, 2));
        for (int i = 0; i < labels.length; ++i) {
            panel.add(new JLabel(labels[i]));
            inputs[i] = new JTextField();
            panel.add(inputs[i]);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Nuevo", 2, -1);
        if (result == 0) {
            String[] newData = new String[labels.length];
            for (int i = 0; i < labels.length; ++i) {
                newData[i] = inputs[i].getText();
            }
            dataMap.put(newData[0], (String[])newData);
            comboBox.addItem(newData[0]);
            try (FileWriter writer = new FileWriter(filePath, true);){
                writer.write(String.join((CharSequence)",", newData) + "\n");
            }
            catch (IOException var20) {
                JOptionPane.showMessageDialog(this, "Error al guardar en el archivo CSV.", "Error", 0);
                var20.printStackTrace();
            }
        }
    }

    public Map<String, String> getFormData() {
        HashMap<String, String> formData = new HashMap<String, String>();
        String[] metadataKeysInstalador = new String[]{"IA1", "IA2", "IA3", "IA4", "IA5", "IA6", "IA7", "IA8", "IA9", "IA10", "IA11", "IA12", "IA13", "IA14", "IA15", "IA16", "IA17", "IA18"};
        for (int i = 0; i < metadataKeysInstalador.length; ++i) {
            formData.put(metadataKeysInstalador[i], fieldsInstalador[i].getText());
        }
        String[] metadataKeysEmpresa = new String[]{"REFEI", "EI1", "EI2", "EI3", "EI4", "EI5", "EI6", "EI7", "EI8", "EI9", "EI10", "EI11", "EI12", "EI13", "EI14", "EI15", "EI16", "EI17", "EI18"};
        for (int i = 0; i < metadataKeysEmpresa.length; ++i) {
            formData.put(metadataKeysEmpresa[i], fieldsEmpresa[i].getText());
        }
        return formData;
    }
}
