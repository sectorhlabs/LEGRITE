/*
 * Decompiled with CFR 0.152.
 */
package ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TitularPanel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Map<String, JTextField> fieldsMap = new HashMap<String, JTextField>();

    public TitularPanel() {
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        this.agregarTitulo("DATOS DEL TITULAR DE LA INSTALACI\u00d3N", gbc, 0);
        String[] labelsTitular = new String[]{"Primer apellido", "Segundo apellido", "Nombre o raz\u00f3n social", "NIF", "Tipo de la v\u00eda", "Nombre de la v\u00eda", "N\u00famero", "Bloque", "Portal", "Escalera", "Piso", "Puerta", "Localidad", "Provincia", "C\u00f3digo postal", "Tel\u00e9fono", "E-mail"};
        String[] metadataKeysTitular = new String[]{"TI1", "TI2", "TI3", "TI4", "TI6", "TI7", "TI8", "TI9", "TI10", "TI11", "TI12", "TI13", "TI14", "TI15", "TI16", "TI17", "TI18"};
        this.agregarCampos(labelsTitular, metadataKeysTitular, gbc, 1);
        int filaInstalacion = labelsTitular.length + 1;
        JLabel tituloInstalacion = new JLabel("DATOS DE LA INSTALACI\u00d3N");
        tituloInstalacion.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        gbc.gridy = filaInstalacion;
        gbc.gridwidth = 1;
        this.add((Component)tituloInstalacion, gbc);
        JButton copiarDatosButton = new JButton("Mismos datos");
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        this.add((Component)copiarDatosButton, gbc);
        String[] labelsInstalacion = new String[]{"Tipo de la v\u00eda", "Nombre de la v\u00eda", "N\u00famero", "Bloque", "Portal", "Escalera", "Piso", "Puerta", "Localidad", "Provincia", "C\u00f3digo postal", "N\u00famero de locales climatizados", "Vivienda aislada", "Vivienda en bloque", "N\u00famero de dormitorios: 1", "N\u00famero de dormitorios: 2", "N\u00famero de dormitorios: 3", "N\u00famero de dormitorios: 4", "Ubicaci\u00f3n de la condensadora"};
        String[] metadataKeysInstalacion = new String[]{"DI6", "DI7", "DI8", "DI9", "DI10", "DI11", "DI12", "DI13", "DI14", "DI15", "DI16", "DI17", "DI18", "DI19", "DI20", "DI21", "DI22", "DI23", "DI24"};
        this.agregarCampos(labelsInstalacion, metadataKeysInstalacion, gbc, filaInstalacion + 1);
        copiarDatosButton.addActionListener(e -> this.copiarDatosTitularAInstalacion());
    }

    private void copiarDatosTitularAInstalacion() {
        String[] camposTitular = new String[]{"TI6", "TI7", "TI8", "TI9", "TI10", "TI11", "TI12", "TI13", "TI14", "TI15", "TI16"};
        String[] camposInstalacion = new String[]{"DI6", "DI7", "DI8", "DI9", "DI10", "DI11", "DI12", "DI13", "DI14", "DI15", "DI16"};
        for (int i = 0; i < camposTitular.length; ++i) {
            JTextField fieldTitular = this.fieldsMap.get(camposTitular[i]);
            JTextField fieldInstalacion = this.fieldsMap.get(camposInstalacion[i]);
            if (fieldTitular == null || fieldInstalacion == null) continue;
            fieldInstalacion.setText(fieldTitular.getText());
        }
    }

    private void agregarTitulo(String titulo, GridBagConstraints gbc, int fila) {
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", 1, 14));
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        this.add((Component)tituloLabel, gbc);
    }

    private void agregarCampos(String[] labels, String[] metadataKeys, GridBagConstraints gbc, int filaInicial) {
        for (int i = 0; i < labels.length; ++i) {
            JLabel label = new JLabel(labels[i]);
            gbc.gridx = 0;
            gbc.gridy = filaInicial + i;
            gbc.gridwidth = 1;
            this.add((Component)label, gbc);
            JTextField field = new JTextField(20);
            gbc.gridx = 1;
            this.add((Component)field, gbc);
            this.fieldsMap.put(metadataKeys[i], field);
        }
    }

    public Map<String, String> getFormData() {
        HashMap<String, String> formData = new HashMap<String, String>();
        for (Map.Entry<String, JTextField> entry : this.fieldsMap.entrySet()) {
            formData.put(entry.getKey(), entry.getValue().getText().trim());
        }
        return formData;
    }
}
