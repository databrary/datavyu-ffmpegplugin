/*
 */

package org.openshapa.views.continuous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;


/**
 *
 */
public final class ProjectDescriptor {

    //--------------------------------------------------------------------------
    // [static] class
    //

    /**
     * Entry describing each data stream and associated viewer plugin.
     */
    public static class Entry {

        /**
         * Main Consructor.
         *
         * @param fields Map of key/values.
         * @throws ClassNotFoundException if unable to find plugin class.
         * @throws FileNotFoundException if path does not point to actual file.
         */
        Entry(final Map<String, Object> fields)
                throws ClassNotFoundException, FileNotFoundException {
            this(
                    new File(
                            (String) fields.get("baseDir"),
                            (String) fields.get("path")
                        ).getAbsolutePath(),
                    (String) fields.get("plugin"),
                    fields.containsKey("offset")
                            ? (long) ((Integer) fields.get("offset"))
                            : 0L
                );
        }

        /**
         * Helper [private] constructor.
         *
         * @param path of data stream.
         * @param plugin to generate data viewer.
         * @param offset of start time for data stream.
         *
         * @throws ClassNotFoundException if unable to find plugin class.
         * @throws FileNotFoundException if path does not point to actual file.
         */
        private Entry(
                final String path,
                final String plug,
                final long ms
        ) throws ClassNotFoundException, FileNotFoundException {
            this.file = new File(path);
            if (!file.exists()) { throw new FileNotFoundException(); }
            this.plugin = Class.forName(plug);
            this.offset = ms;
        }

        public File file;
        public Class plugin;
        public long offset;
    }


    //--------------------------------------------------------------------------
    // [private] members
    //

    private String basePath;

    /** Project descriptors entries. */
    private List<Entry> entries = new ArrayList<Entry>();


    //--------------------------------------------------------------------------
    // initialization
    //

    /**
     *
     */
    public ProjectDescriptor() { }


    //--------------------------------------------------------------------------
    // [public] interface
    //

    public void setBasePath(final String base) { basePath = base; }

    /**
     * Process the project descriptor generating data entries.
     *
     * @param reader Input source.
     */
    public void process(final java.io.Reader reader) {
        Yaml yaml = new Yaml();

        List<Map<String, Object>> data
                = (List<Map<String, Object>>) yaml.load(reader);
        for (Map<String, Object> entry : data) {
            entry.put("baseDir", basePath);
            try {
                entries.add(new Entry(entry));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ProjectDescriptor.class.getName())
                        .log(Level.SEVERE, entry.toString(), ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ProjectDescriptor.class.getName())
                        .log(Level.SEVERE, entry.toString(), ex);
            }
        }
    }

    /**
     * @return the entries.
     */
    public Iterable<Entry> getEntries() { return entries; }


    //--------------------------------------------------------------------------
}
