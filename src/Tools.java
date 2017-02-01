
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author CCRPTS
 */
public abstract class Tools {

    static final String LABELS_FILE = "labels.dat";

    public static void serializeLables(ArrayList<Label> labels) throws FileNotFoundException, IOException {
        checkFile();

        try (FileOutputStream fos = new FileOutputStream(LABELS_FILE); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(labels);
        }
    }

    public static ArrayList<Label> deserializeLabels() throws FileNotFoundException, IOException, ClassNotFoundException {
        checkFile();
        try {
            ArrayList<Label> labels;
            try (FileInputStream fis = new FileInputStream(LABELS_FILE); ObjectInputStream ois = new ObjectInputStream(fis)) {
                labels = (ArrayList) ois.readObject();
            }

            return labels;
        } catch (IOException | ClassNotFoundException ex) {
            return new ArrayList<>();
        }
    }

    public static void parse(File inFile, File outFile, boolean generateHr, boolean downloadImages, boolean uppercase, String fDelimiter, String eDelimiter) throws FileNotFoundException, IOException, ClassNotFoundException {

        StringBuilder csv = new StringBuilder();
        StringBuilder hr = new StringBuilder();

        ArrayList<Label> labels = deserializeLabels();

        Scanner input = new Scanner(inFile);
        int processed = 0;
        int imageCount = 0;
        while (input.hasNextLine()) {
            System.out.println();
            String line = input.nextLine();
            System.out.print("Parsing line: " + line);

            //Check to see if there's a url to download
            if (downloadImages && (line.contains(".png") || line.contains(".jpg"))) {
                if (line.split(fDelimiter).length > 1) {
                    File dir = new File("images/");
                    dir.mkdir();
                    System.out.println("\nDownloading from " + line.split(fDelimiter)[1].trim());
                    downloadImage("http:" + line.substring(line.indexOf("//")).trim(), "images/" + processed + "_image" + imageCount + ".png");
                    imageCount++;
                }
            }

            //Check to see if the line contains the entry delimiter
            if (line.contains(eDelimiter)) {
                processed++;
                imageCount = 0;
                System.out.println(" - entry delimiter found, creating new entry");
                csv = new StringBuilder(csv.substring(0, csv.length() - 1));
                csv.append("\n");
                hr.append("\n").append(eDelimiter).append("\n");
                continue;
            }

            //Check to see if the line is blank
            if (line.equals("") || line.split(fDelimiter).length < 2) {
                continue;
            }
            String key = line.split(fDelimiter)[0].toLowerCase();
            String value = line.split(fDelimiter)[1];

            for (Label l : labels) {
                if (key.contains(l.getLabel().toLowerCase())) {
                    boolean excluded = false;

                    for (String s : l.getExclusions()) {

                        if (key.toLowerCase().contains(s.toLowerCase()) && l.getExclusions().length > 0) {
                            excluded = true;
                            break;
                        }
                    }

                    if (!excluded) {
                        value = value.trim();
                        if (uppercase) {
                            value = value.toUpperCase();
                        }
                        csv.append(value).append(",");
                        hr.append(l.getDescription()).append(": ").append(value).append("\n");
                    }

                }
            }
        }

        PrintStream stream = new PrintStream(outFile);
        stream.println(csv.substring(0, csv.length() - 1));
        stream.close();

        if (generateHr) {
            File newFile = new File(outFile.getCanonicalPath().substring(0, outFile.getCanonicalPath().indexOf(".")) + ".txt");
            stream = new PrintStream(newFile);
            stream.println(hr.toString());
            stream.close();
        }

    }

    public static void downloadImage(String url, String name) throws MalformedURLException, IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(name));
        }
    }

    private static void checkFile() throws IOException {
        if (!new File(LABELS_FILE).exists()) {
            new File(LABELS_FILE).createNewFile();
        }
    }
}
