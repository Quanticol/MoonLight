package eu.quanticol.moonlight.examples.subway.io;

import eu.quanticol.moonlight.examples.subway.parsing.PrintingStrategy;

import java.io.*;

/**
 * Generic Class to write line-wise from a text file.
 * given an object conformant to a parsing strategy,
 * it updates the output file
 *
 * @param <T> type to which the data read is converted
 *
 * @see PrintingStrategy
 */
public class DataWriter<T> {

    private final FileType type;
    private final String path;
    private final PrintingStrategy<T> strategy;

    /**
     * Writer initialization
     * @param path the path to the data source file
     * @param type the file type of source
     * @param strategy the printing strategy to execute
     */
    public DataWriter(String path,
                      FileType type,
                      PrintingStrategy<T> strategy) {
        this.path = path;
        this.type = type;
        this.strategy = strategy;
    }

    /**
     * Writes the data to the destination file
     * @param data to write
     */
    public void write(T data) {
        try {
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(path));

            // Write the header
            writeHeader(outWriter, data);

            // Write the data
            writeLines(outWriter, data);

            outWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader(BufferedWriter outWriter, T header)
            throws IOException
    {
        outWriter.write(strategy.initialize(header, getWordBreak()));
    }

    private void writeLines(BufferedWriter outWriter, T data)
            throws IOException
    {

        String output = strategy.print(data, getWordBreak());
        while(null != output) {
            outWriter.write(output + "\n");
            output = strategy.print(data, getWordBreak());
        }
    }

    private String getWordBreak() {
        return FileType.CSV == type ? "," : " ";
    }
}
