package org.kew.shs.dedupl.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

@SuppressWarnings("serial")
public class Dictionary extends HashMap<String, String> {

    String fileDelimiter;
    String filePath;

    public void readFile() throws IOException {
        CsvPreference customCsvPref = new CsvPreference.Builder('"', this.getFileDelimiter().charAt(0), "\n").build();
        try (CsvListReader reader = new CsvListReader(new FileReader(this.getFilePath()), customCsvPref)) {
            List<String> miniMap;
            while ((miniMap = reader.read()) != null) {
                this.put(miniMap.get(0), miniMap.get(1));
            }
        }
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
