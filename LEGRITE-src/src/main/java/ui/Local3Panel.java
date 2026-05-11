/*
 * Decompiled with CFR 0.152.
 */
package ui;

import data.MaquinasCsvReader;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Local3Panel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField nombreLocalField;
    JComboBox<String> comboMaquinas;
    private JComboBox<String> comboModelos;
    private JTextField[] fieldsAtributos;
    private final String[] labelsAtributos = new String[]{"Potencia calefaccion kW", "SCOP", "Potencia frigorifica kW", "Potencia de compresor kW", "Prestacion energetica", "SEER", "Potencia electrica nominal kW", "Tipo de maquina", "Ficha tecnica", "COP", "EER", "Declaracion conformidad", "Tuberia gas", "Tuberia liquido", "Carga de refrigerante Kg"};
    private Map<String, Map<String, String>> modelosData;
    private final String directorioTrabajo;

    public Local3Panel(Map<String, List<String>> maquinasData, String directorioTrabajo) {
        this.directorioTrabajo = directorioTrabajo;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel tituloLocal = new JLabel("DATOS DEL LOCAL 3");
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
        this.comboMaquinas = new JComboBox();
        this.comboMaquinas.addItem("");
        ArrayList<String> maquinasOrdenadas = new ArrayList<String>(maquinasData.keySet());
        Collections.sort(maquinasOrdenadas);
        for (String maquina : maquinasOrdenadas) {
            this.comboMaquinas.addItem(maquina);
        }
        this.comboMaquinas.setSelectedIndex(0);
        gbc.gridx = 1;
        this.add(this.comboMaquinas, gbc);
        JLabel labelModelo = new JLabel("Seleccionar Modelo:");
        gbc.gridx = 0;
        ++gbc.gridy;
        this.add((Component)labelModelo, gbc);
        this.comboModelos = new JComboBox();
        this.comboModelos.addItem("");
        this.comboModelos.setSelectedIndex(0);
        gbc.gridx = 1;
        this.add(this.comboModelos, gbc);
        this.comboMaquinas.addActionListener(e -> {
            block6: {
                block4: {
                    block5: {
                        String selectedMaquina = (String)this.comboMaquinas.getSelectedItem();
                        if (selectedMaquina == null || selectedMaquina.trim().isEmpty()) break block4;
                        this.modelosData = this.loadMachineData(selectedMaquina);
                        if (this.modelosData == null || this.modelosData.isEmpty()) break block5;
                        this.comboModelos.removeAllItems();
                        this.comboModelos.addItem("");
                        for (String modelo : this.modelosData.keySet()) {
                            this.comboModelos.addItem(modelo);
                        }
                        this.comboModelos.setSelectedIndex(0);
                        break block6;
                    }
                    this.comboModelos.removeAllItems();
                    this.comboModelos.addItem("");
                    this.comboModelos.setSelectedIndex(0);
                    if (this.fieldsAtributos == null) break block6;
                    for (JTextField field : this.fieldsAtributos) {
                        field.setText("");
                    }
                    break block6;
                }
                this.comboModelos.removeAllItems();
                this.comboModelos.addItem("");
                this.comboModelos.setSelectedIndex(0);
                if (this.fieldsAtributos != null) {
                    for (JTextField field : this.fieldsAtributos) {
                        field.setText("");
                    }
                }
            }
        });
        this.comboModelos.addActionListener(e -> {
            block4: {
                block3: {
                    String selectedModelo = (String)this.comboModelos.getSelectedItem();
                    if (selectedModelo == null || selectedModelo.trim().isEmpty()) break block3;
                    if (this.modelosData == null || !this.modelosData.containsKey(selectedModelo)) break block4;
                    Map<String, String> atributos = this.modelosData.get(selectedModelo);
                    for (int ix = 0; ix < this.labelsAtributos.length; ++ix) {
                        this.fieldsAtributos[ix].setText(atributos.getOrDefault(this.labelsAtributos[ix], ""));
                    }
                    break block4;
                }
                if (this.fieldsAtributos != null) {
                    for (JTextField field : this.fieldsAtributos) {
                        field.setText("");
                    }
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

    private Map<String, Map<String, String>> loadMachineData(String maquina) {
        String csvPath = this.directorioTrabajo + "/maquinas/" + maquina + ".csv";
        File file = new File(csvPath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Archivo CSV no encontrado para la m\u00e1quina: " + maquina, "Error", 0);
            return null;
        }
        return MaquinasCsvReader.readCsvFromFile(csvPath);
    }

    public Map<String, String> getFormData() {
        HashMap<String, String> formData = new HashMap<String, String>();
        formData.put("NLC_3", this.nombreLocalField.getText());
        formData.put("MRAA_3", (String)this.comboMaquinas.getSelectedItem());
        formData.put("MAA1_3", (String)this.comboModelos.getSelectedItem());
        for (int i = 0; i < this.labelsAtributos.length; ++i) {
            formData.put("MAA" + (i + 2) + "_3", this.fieldsAtributos[i].getText());
        }
        return formData;
    }
}
