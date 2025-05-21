package service;

import model.ConversionRecord;
import java.util.ArrayList;
import java.util.List;

public class ConversionHistory {
    private static final int MAX_RECORDS = 10;
    private final List<ConversionRecord> history;

    public ConversionHistory() {
        this.history = new ArrayList<>();
    }

    public void addRecord(ConversionRecord record) {
        if (history.size() >= MAX_RECORDS) {
            history.remove(0);
        }
        history.add(record);
    }

    public List<ConversionRecord> getHistory() {
        return new ArrayList<>(history);
    }

    public void clearHistory() {
        history.clear();
    }
}