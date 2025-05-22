package org.opencourse.services;

import org.opencourse.models.History;
import org.opencourse.repositories.HistoryRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * History service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class HistoryManager {

    private final HistoryRepo historyRepo; // Data access object.

    /**
     * Constructor.
     * 
     * @param historyRepo The history repository
     */
    @Autowired
    public HistoryManager(HistoryRepo historyRepo) {
        this.historyRepo = historyRepo;
    }

    /**
     * Add a new history record.
     */
    public History addHistory() {
        return null; // TODO: Implement this method.
    }

    /**
     * Get histories.
     */
    public List<History> getHistories() {
        return historyRepo.findAll(); // TODO: Implement this method.
    }
    
}
