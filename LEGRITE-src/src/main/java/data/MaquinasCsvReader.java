/*
 * Decompiled with CFR 0.152.
 */
package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaquinasCsvReader {
    /*
     * Enabled aggressive exception aggregation
     */
    public static Map<String, Map<String, String>> readCsvFromFile(String filePath) {
        LinkedHashMap<String, Map<String, String>> data = new LinkedHashMap<String, Map<String, String>>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));){
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.isEmpty()) {
                System.out.println("El archivo CSV est\u00e1 vac\u00edo: " + filePath);
                LinkedHashMap<String, Map<String, String>> linkedHashMap = data;
                return linkedHashMap;
            }
            headerLine = headerLine.replace("\ufeff", "").trim();
            CharSequence[] headers = headerLine.split(",", -1);
            System.out.println("Cabecera detectada: " + String.join((CharSequence)", ", headers));
            if (headers.length > 1 && ((String)headers[0]).equalsIgnoreCase("Modelo")) {
                String line;
                while ((line = br.readLine()) != null) {
                    if ((line = line.trim()).isEmpty()) continue;
                    String[] values = line.split(",", -1);
                    if (values.length <= 1) {
                        System.out.println("L\u00ednea malformada ignorada: " + line);
                        continue;
                    }
                    String atributo = values[0].trim();
                    for (int col = 1; col < headers.length; ++col) {
                        String modelo = ((String)headers[col]).trim();
                        String valor = col < values.length ? values[col].trim() : "";
                        data.computeIfAbsent(modelo, k -> new LinkedHashMap()).put(atributo, valor);
                    }
                }
                LinkedHashMap<String, Map<String, String>> linkedHashMap = data;
                return linkedHashMap;
            }
            System.out.println("El archivo CSV no contiene un encabezado v\u00e1lido en: " + filePath);
            LinkedHashMap<String, Map<String, String>> linkedHashMap = data;
            return linkedHashMap;
        }
        catch (IOException var21) {
            System.out.println("Error al leer el archivo CSV: " + filePath);
            var21.printStackTrace();
            return data;
        }
    }
}
