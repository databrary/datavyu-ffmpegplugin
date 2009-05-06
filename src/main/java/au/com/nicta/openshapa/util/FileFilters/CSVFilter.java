package au.com.nicta.openshapa.util.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author cfreeman
 */
public class CSVFilter extends FileFilter {
    public String getDescription() {
        return new String("CSV file");
    }

    public boolean accept(File dir) {
        return (dir.getName().endsWith(".csv"));
    }
}
