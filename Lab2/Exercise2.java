import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Spliterator;

public class Exercise2 {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private OptimaizeLangDetector langDetector;

    public static void main(String[] args) {
        Exercise2 exercise = new Exercise2();
        exercise.run();
    }

    private void run() {
        try {
            if (!new File("./outputDocuments").exists()) {
                Files.createDirectory(Paths.get("./outputDocuments"));
            }

            initLangDetector();

            File directory = new File("./documents/ok");
            File[] files = directory.listFiles();
            assert files != null;
            for (File file : files) {
                processFile(file);
            }


        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }

    }

    private void initLangDetector() throws IOException {
        // TODO initialize language detector (langDetector)
    }

    private void processFile(File file) throws IOException, SAXException, TikaException {
        // TODO: extract content, metadata and language from given file
        // call saveResult method to save the data

        saveResult(file.getName(), getLanguage(file), null, getCreationDate(file), getLastModification(file), null, null); //TODO: fill with proper values
    }

    private void saveResult(String fileName, String language, String creatorName, String creationDate,
                            String lastModification, String mimeType, String content) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int index = fileName.lastIndexOf(".");
        String outName = fileName.substring(0, index) + ".txt";
        try {
            PrintWriter printWriter = new PrintWriter("./outputDocuments/" + outName);
            printWriter.write("Name: " + fileName + "\n");
            printWriter.write("Language: " + (language != null ? language : "") + "\n");
            printWriter.write("Creator: " + (creatorName != null ? creatorName : "") + "\n");
            String creationDateStr = creationDate == null ? "" : creationDate;
            printWriter.write("Creation date: " + creationDateStr + "\n");
            String lastModificationStr = lastModification == null ? "" : lastModification;
            printWriter.write("Last modification: " + lastModificationStr + "\n");
            printWriter.write("MIME type: " + (mimeType != null ? mimeType : "") + "\n");
            printWriter.write("\n");
            printWriter.write(content + "\n");
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getLanguage(File file) throws TikaException, SAXException, IOException {

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        if (file.isFile()) {
            FileInputStream content = new FileInputStream(file);
            //Parsing the given document
            parser.parse(content, handler, metadata, new ParseContext());

            LanguageIdentifier object = new LanguageIdentifier(handler.toString());
            //System.out.println(file + " Language name: " + object.getLanguage());
            return object.getLanguage();
        } else return null;
    }

    public String getCreator(File file) throws IOException {
        FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(Path.of("./documents/ok"), FileOwnerAttributeView.class);
        UserPrincipal owner = ownerAttributeView.getOwner();
        System.out.println("owner: " + owner.getName());
        return owner.getName();
    }

    public String getCreationDate(File file) throws IOException, TikaException, SAXException {

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        //System.out.println(file.getName());
        InputStream is = new BufferedInputStream(new FileInputStream(file));

        parser.parse(is, handler, metadata, new ParseContext());
        String creationDate = metadata.get(Metadata.CREATION_DATE);
//        System.out.println("Creation Date: " + creationDate);

        try {
            String[] parts = creationDate.split("T");
            String date = parts[0];
            return date;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLastModification(File file) throws IOException, TikaException, SAXException {

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        //System.out.println(file.getName());
        InputStream is = new BufferedInputStream(new FileInputStream(file));

        parser.parse(is, handler, metadata, new ParseContext());
        String lastModifiedDate = metadata.get(Metadata.LAST_MODIFIED);

        try {
            String[] parts = lastModifiedDate.split("T");
            String date = parts[0];
            return date;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
