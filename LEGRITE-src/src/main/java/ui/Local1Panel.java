/*
 * Decompiled with CFR 0.152.
 */
package ui;

import data.MaquinasCsvReader;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Local1Panel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField nombreLocalField;
    JComboBox<String> comboMaquinas;
    private JComboBox<String> comboModelos;
    private JTextField[] fieldsAtributos;
    private final String[] labelsAtributos = new String[]{"Potencia calefaccion kW", "SCOP", "Potencia frigorifica kW", "Potencia de compresor kW", "Prestacion energetica", "SEER", "Potencia electrica nominal kW", "Tipo de maquina", "Ficha tecnica", "COP", "EER", "Declaracion conformidad", "Tuberia gas", "Tuberia liquido", "Carga de refrigerante Kg"};
    private Map<String, Map<String, String>> modelosData;
    private final String directorioTrabajo;

    public Local1Panel(Map<String, List<String>> maquinasData, String directorioTrabajo) {
        this.directorioTrabajo = directorioTrabajo;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel tituloLocal = new JLabel("DATOS DEL LOCAL 1");
        tituloLocal.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.add((Component)tituloLocal, gbc);
        JLabel labelNombreLocal = new JLabel("Nombre del Local:");
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 1;
        this.add((Component)labelNombreLocal, gbc);
        this.nombreLocalField = new JTextField(20);
        gbc.gridx = 1;
        this.add((Component)this.nombreLocalField, gbc);
        JLabel labelMaquina = new JLabel("Seleccionar M\u00e1quina:");
        gbc.gridx = 0;
        ++gbc.gridy;
        this.add((Component)labelMaquina, gbc);
        ArrayList<String> maquinasOrdenadas = new ArrayList<String>(maquinasData.keySet());
        Collections.sort(maquinasOrdenadas);
        this.comboMaquinas = new JComboBox<String>(maquinasOrdenadas.toArray(new String[0]));
        gbc.gridx = 1;
        this.add(this.comboMaquinas, gbc);
        JLabel labelModelo = new JLabel("Seleccionar Modelo:");
        gbc.gridx = 0;
        ++gbc.gridy;
        this.add((Component)labelModelo, gbc);
        this.comboModelos = new JComboBox();
        gbc.gridx = 1;
        this.add(this.comboModelos, gbc);
        JButton addMachineButton = new JButton("Agregar M\u00e1quina");
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 2;
        this.add((Component)addMachineButton, gbc);
        addMachineButton.addActionListener(e -> this.showAddMachineDialog());
        this.comboMaquinas.addActionListener(e -> {
            String selectedMaquina = (String)this.comboMaquinas.getSelectedItem();
            this.modelosData = this.loadMachineData(selectedMaquina);
            if (this.modelosData != null && !this.modelosData.isEmpty()) {
                this.comboModelos.removeAllItems();
                for (String modelo : this.modelosData.keySet()) {
                    this.comboModelos.addItem(modelo);
                }
            }
        });
        this.comboModelos.addActionListener(e -> {
            String selectedModelo = (String)this.comboModelos.getSelectedItem();
            if (selectedModelo != null && this.modelosData.containsKey(selectedModelo)) {
                Map<String, String> atributos = this.modelosData.get(selectedModelo);
                for (int ix = 0; ix < this.labelsAtributos.length; ++ix) {
                    this.fieldsAtributos[ix].setText(atributos.getOrDefault(this.labelsAtributos[ix], ""));
                }
            }
        });
        JLabel tituloAtributos = new JLabel("Atributos del Modelo:");
        tituloAtributos.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 2;
        this.add((Component)tituloAtributos, gbc);
        this.fieldsAtributos = new JTextField[this.labelsAtributos.length];
        for (int i = 0; i < this.labelsAtributos.length; ++i) {
            JLabel labelAtributo = new JLabel(this.labelsAtributos[i] + ":");
            gbc.gridx = 0;
            ++gbc.gridy;
            gbc.gridwidth = 1;
            this.add((Component)labelAtributo, gbc);
            this.fieldsAtributos[i] = new JTextField(20);
            gbc.gridx = 1;
            this.add((Component)this.fieldsAtributos[i], gbc);
        }
    }

    private void showAddMachineDialog() {
        JDialog addMachineDialog = new JDialog((Frame)null, "Agregar M\u00e1quina o Modelo", true);
        addMachineDialog.setLayout(new GridBagLayout());
        addMachineDialog.setSize(600, 700);
        addMachineDialog.setLocationRelativeTo(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        addMachineDialog.add((Component)new JLabel("Nombre de la M\u00e1quina:"), gbc);
        JTextField newMachineField = new JTextField(20);
        gbc.gridx = 1;
        addMachineDialog.add((Component)newMachineField, gbc);
        gbc.gridx = 0;
        ++gbc.gridy;
        addMachineDialog.add((Component)new JLabel("Nuevo Modelo:"), gbc);
        JTextField newModelField = new JTextField(20);
        gbc.gridx = 1;
        addMachineDialog.add((Component)newModelField, gbc);
        LinkedHashMap<String, JTextField> attributeFields = new LinkedHashMap<String, JTextField>();
        for (String attribute : this.labelsAtributos) {
            gbc.gridx = 0;
            ++gbc.gridy;
            addMachineDialog.add((Component)new JLabel(attribute + ":"), gbc);
            JTextField field = new JTextField(20);
            attributeFields.put(attribute, field);
            gbc.gridx = 1;
            addMachineDialog.add((Component)field, gbc);
        }
        JButton saveButton = new JButton("Guardar");
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 2;
        saveButton.addActionListener(e -> {
            String machineName = newMachineField.getText().trim();
            String newModel = newModelField.getText().trim();
            if (!machineName.isEmpty() && !newModel.isEmpty()) {
                String maquinasDirPath = this.directorioTrabajo + File.separator + "maquinas";
                File maquinasDir = new File(maquinasDirPath);
                if (!maquinasDir.exists()) {
                    maquinasDir.mkdirs();
                }
                String csvPath = maquinasDirPath + File.separator + machineName + ".csv";
                try (PrintWriter writer = new PrintWriter(new File(csvPath));){
                    String[] modelos;
                    writer.print("Modelo");
                    for (String model : modelos = newModel.split(",")) {
                        writer.print("," + model.trim());
                    }
                    writer.println();
                    for (String attribute : this.labelsAtributos) {
                        writer.print(attribute);
                        String value = ((JTextField)attributeFields.get(attribute)).getText().trim();
                        for (int i = 0; i < modelos.length; ++i) {
                            writer.print("," + (value.isEmpty() ? " " : value));
                        }
                        writer.println();
                    }
                    JOptionPane.showMessageDialog(addMachineDialog, "Nueva m\u00e1quina creada correctamente.", "\u00c9xito", 1);
                }
                catch (IOException e1) {
                    JOptionPane.showMessageDialog(addMachineDialog, "Error al crear la m\u00e1quina: " + e1.getMessage(), "Error", 0);
                    return;
                }
                String indexPath = maquinasDirPath + File.separator + "index.txt";
                try (FileWriter indexWriter = new FileWriter(indexPath, true);){
                    indexWriter.write(machineName + ".csv\n");
                }
                catch (IOException e2) {
                    JOptionPane.showMessageDialog(addMachineDialog, "Error al actualizar el archivo index.txt: " + e2.getMessage(), "Error", 0);
                }
                addMachineDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(addMachineDialog, "El nombre de la m\u00e1quina y modelo son obligatorios.", "Error", 0);
            }
        });
        addMachineDialog.add((Component)saveButton, gbc);
        addMachineDialog.setVisible(true);
    }

    private Map<String, Map<String, String>> loadMachineData(String maquina) {
        String csvPath = this.directorioTrabajo + "/maquinas/" + maquina + ".csv";
        System.out.println("Cargando archivo CSV desde: " + csvPath);
        File file = new File(csvPath);
        if (!file.exists()) {
            System.out.println("Archivo no encontrado: " + csvPath);
            JOptionPane.showMessageDialog(this, "Archivo CSV no encontrado para la m\u00e1quina: " + maquina, "Error", 0);
            return null;
        }
        return MaquinasCsvReader.readCsvFromFile(csvPath);
    }

    public Map<String, String> getFormData() {
        HashMap<String, String> formData = new HashMap<String, String>();
        formData.put("NLC_1", this.nombreLocalField.getText());
        formData.put("MRAA_1", (String)this.comboMaquinas.getSelectedItem());
        formData.put("MAA1_1", (String)this.comboModelos.getSelectedItem());
        for (int i = 0; i < this.labelsAtributos.length; ++i) {
            formData.put("MAA" + (i + 2) + "_1", this.fieldsAtributos[i].getText());
        }
        return formData;
    }
}
