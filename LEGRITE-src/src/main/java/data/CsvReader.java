/*
 * Decompiled with CFR 0.152.
 */
package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CsvReader {
    public static Map<String, String[]> readCsv(String filePath) {
        HashMap<String, String[]> data = new HashMap<String, String[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));){
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                if (values.length < 3) continue;
                String fullName = values[0].trim() + " " + values[1].trim() + " " + values[2].trim();
                data.put(fullName, values);
            }
        }
        catch (Exception var16) {
            System.out.println("Error al leer el archivo CSV: " + filePath);
            var16.printStackTrace();
        }
        return data;
    }
}
