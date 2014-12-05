package AssemblerDisassembler;

import java.io.File;
import javax.swing.filechooser.FileFilter;
 
public class FileTypeFilter extends FileFilter {
 
    private String fileExtension;
    private String description;
     
    public FileTypeFilter(String extension, String description) {
        this.fileExtension = extension;
        this.description = description;
    }
     
    @Override
    public boolean accept(File file) {
    	//if a file is still a folder
        if (file.isDirectory()) {
            return true;
        }
        return file.getName().toLowerCase().endsWith(fileExtension);
    }
     
    public String getDescription() {
        return description + String.format(" (*%s)", fileExtension);
    }
}
