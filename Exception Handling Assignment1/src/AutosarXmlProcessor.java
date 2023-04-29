import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutosarXmlProcessor {

    public static void main(String[] args) {
        try {
            String filename = args[0];
            processAutosarXmlFile(filename);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please provide the input filename as an argument.");
        } catch (NotValidAutosarFileException e) {
            System.out.println(e.getMessage());
        } catch (EmptyAutosarFileException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("An I/O error occurred while processing the file.");
        }
    }

    private static void processAutosarXmlFile(String filename) throws IOException, NotValidAutosarFileException, EmptyAutosarFileException {
        File file = new File(filename);

        // Check if the file is empty
        if (file.length() == 0) {
            throw new EmptyAutosarFileException("The input file is empty.");
        }

        // Check if the file has a valid .arxml extension
        if (!filename.endsWith(".arxml")) {
            throw new NotValidAutosarFileException("The input file does not have a valid .arxml extension.");
        }

        // Read the containers from the file and store them in a list
        List<Container> containers = new ArrayList<>();
        try  {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String id = null;
            String shortName = null;
            String longName = null;
            while ((line = br.readLine()) != null) {

                if (line.contains("<CONTAINER")) {
                    int startIndex = line.indexOf("UUID=\"") + 6;
                    int endIndex = line.indexOf("\"", startIndex);
                    id = line.substring(startIndex, endIndex);
                }

                if (line.contains("<SHORT-NAME>")) {
                    int startIndex = line.indexOf("<SHORT-NAME>") + 12;
                    int endIndex = line.indexOf("</SHORT-NAME>", startIndex);
                     shortName = line.substring(startIndex, endIndex);
                }

                if (line.contains("<LONG-NAME>")) {
                    int startIndex = line.indexOf("<LONG-NAME>") + 11;
                    int endIndex = line.indexOf("</LONG-NAME>", startIndex);
                    longName = line.substring(startIndex, endIndex);
                    }
                if(line.contains("</CONTAINER>")){
                    containers.add(new Container(id, shortName, longName));}
            }
        }
    catch (FileNotFoundException e) {
        System.err.println("Error: " + filename + " not found.");
        System.exit(1);
    } catch (IOException e) {
        System.err.println("Error reading " + filename + ": " + e.getMessage());
        System.exit(1);
    }

        // Sort the containers by their short name
        Collections.sort(containers);
        // Write the sorted containers to a new file
        String outputFilename = getOutputFilename(filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<AUTOSAR>\n");
            for (Container container : containers) {
                bw.write("     <CONTAINER UUID=\"" + container.getId() + "\">\n");
                bw.write("         <SHORT-NAME>" + container.getShortName() + "</SHORT-NAME>\n");
                bw.write("         <LONG-NAME>" + container.getLongName() + "</LONG-NAME>\n");
                bw.write("     </CONTAINER>\n");
            }
            bw.write("</AUTOSAR>");
        }
        System.out.println("The sorted containers have been written to " + outputFilename + ".");
    }

    private static String getOutputFilename(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return filename.substring(0, dotIndex) + "_mod.arxml";
    }

}

class Container implements Comparable<Container>
{
    private String id;
    private String shortName;
    private String longName;

    public Container(String id, String shortName, String longName )  {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
    @Override
    public int compareTo(Container c) {
        if (this.shortName == null && c.shortName == null) {
            return 0;
        } else if (this.shortName == null) {
            return 1;
        } else if (c.shortName == null) {
            return -1;
        } else {
            return this.shortName.compareTo(c.shortName);
        }
    }
}

class NotValidAutosarFileException extends Exception {
    public NotValidAutosarFileException(String message) {
        super(message);
    }
}

class EmptyAutosarFileException extends RuntimeException {
    public EmptyAutosarFileException(String message) {
        super(message);
    }
}



