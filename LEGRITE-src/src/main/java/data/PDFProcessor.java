/*
 * Decompiled with CFR 0.152.
 */
package data;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

public class PDFProcessor {
    public void procesarPDFs(String inputPath, String outputPath, Map<String, String> formData) throws IOException {
        File inputDir = new File(inputPath);
        File[] pdfFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles != null && pdfFiles.length != 0) {
            for (File pdfFile : pdfFiles) {
                try (PDDocument document = Loader.loadPDF(pdfFile);){
                    PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
                    if (acroForm != null) {
                        this.rellenarCampos(acroForm, formData);
                    }
                    String outputFilePath = outputPath + File.separator + pdfFile.getName();
                    document.save(outputFilePath);
                }
            }
        } else {
            throw new IOException("No se encontraron archivos PDF en la carpeta seleccionada.");
        }
    }

    private void rellenarCampos(PDAcroForm acroForm, Map<String, String> formData) throws IOException {
        for (PDField field : acroForm.getFields()) {
            String fieldName = field.getFullyQualifiedName();
            if (!formData.containsKey(fieldName) || formData.get(fieldName).trim().isEmpty()) continue;
            field.setValue(formData.get(fieldName));
        }
    }
}
