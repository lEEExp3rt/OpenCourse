package org.opencourse.services.storage;

import java.io.InputStream;

import org.opencourse.models.Resource;

/**
 * File information class for downloading files.
 * 
 * @author !EEExp3rt
 */
public class FileInfo {

    private InputStream inputStream;
    private String fileName;
    
    /**
     * Constructor.
     *
     * @param inputStream The input stream of the file.
     * @param resource The resource associated with the file.
     */
    public FileInfo(InputStream inputStream, Resource resource) {
        this.inputStream = inputStream;
        String path = resource.getResourceFile().getFilePath();
        this.fileName = path.substring(path.lastIndexOf('/') + 1);
    }

    public InputStream getFile() {
        return inputStream;
    }

    public String getFileName() {
        return fileName;
    }
}
