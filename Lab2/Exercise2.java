import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.vfs2.provider.rar.RARFileObject;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Exercise2 {

    public ArrayList<String> extractedRars = new ArrayList<>();

    public static void main(String[] args) {
        Exercise2 exercise = new Exercise2();
        exercise.run(0);
    }

    private void run(int rar) {
        try {
            if (!new File("./outputDocuments").exists()) {
                Files.createDirectory(Paths.get("./outputDocuments"));
            }

            File directory;

            if (rar == 1) directory = new File("./documents/rars");
            else directory = new File("./documents");

            File[] files = directory.listFiles();
            assert files != null;
            for (File file : files) {
//                if (!extractedRars.contains(file.getName())) {
//                    processFile(file);
//                }
                processFile(file);
            }


        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }

    }

    private void processFile(File file) throws IOException, SAXException, TikaException {
        // TODO: extract content, metadata and language from given file
        // call saveResult method to save the data

        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        if (extension.equals("zip")) {
            ZipFile zipFile = new ZipFile("./documents/" + file.getName());
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                new ZipEntryProcess(entry, zipFile);
            }

//        } else if (extension.equals("rar")) {
//            Archive a = null;
//            try {
//                a = new Archive(new FileVolumeManager(file));
//            } catch (RarException | IOException e) {
//
//                e.printStackTrace();
//            }
//            if (a != null) {
//                a.getMainHeader().print();
//                FileHeader fh = a.nextFileHeader();
//                while (fh != null) {
//                    try {
//                        File out = new File("./documents/rars/"
//                                + fh.getFileNameString().trim());
//                        //System.out.println(out.getAbsolutePath());
//                        if(fh.getFileNameString().contains(".rar")){
//                            processFile(out);
//                        }
//                        extractedRars.add(fh.getFileNameString());
//                        FileOutputStream os = new FileOutputStream(out);
//                        a.extractFile(fh, os);
//                        os.close();
//                    } catch (RarException | IOException e) {
//
//                        e.printStackTrace();
//                    }
//                    fh = a.nextFileHeader();
//                }
//            }
//            run(1);


        } else if (file.isFile()) {

            saveResult(file.getName(), getLanguage(file), getCreator(file), getCreationDate(file), getLastModification(file), getMime(file), getContent(file)); //TODO: fill with proper values
        }
    }

    private void saveResult(String fileName, String language, String creatorName, String creationDate,
                            String lastModification, String mimeType, String content) {

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

    public String getLanguage(File file) throws SAXException, IOException {

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            if (file.isFile()) {
                FileInputStream content = new FileInputStream(file);
                //Parsing the given document
                parser.parse(content, handler, metadata, new ParseContext());

                LanguageIdentifier object = new LanguageIdentifier(handler.toString());
                //System.out.println(file + " Language name: " + object.getLanguage());
                return object.getLanguage();
            } else return null;
        } catch (NullPointerException | TikaException e) {
            return null;
        }
    }

    public String getCreator(File file) throws IOException, TikaException, SAXException {

        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        //System.out.println(file.getName());
        InputStream is = new BufferedInputStream(new FileInputStream(file));

        parser.parse(is, handler, metadata, new ParseContext());
        String creator = metadata.get(Metadata.CREATOR);

        return creator;
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

    public String getMime(File file) throws IOException, TikaException, SAXException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file);
        return mimeType;
    }

    public String getContent(File file) throws IOException {
        BodyContentHandler handler = new BodyContentHandler();
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            parser.parse(is, handler, metadata);
            String text = handler.toString();
            return text;
        } catch (SAXException | IOException | TikaException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return null;
    }

    class ZipEntryProcess {

        public ZipFile zf;

        public ZipEntryProcess(ZipEntry file, ZipFile zf) throws IOException, TikaException, SAXException {
            this.zf = zf;

            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i + 1);
            }
            if (extension.equals("zip")) {
                ZipFile zipFile = new ZipFile("./documents/" + file.getName());
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();

                    new ZipEntryProcess(entry, zipFile);
                }
            } else {

                saveZipEntryResult(file.getName(), getZipEntryLanguage(file), getZipEntryCreator(file), getZipEntryCreationDate(file), getZipEntryLastModification(file), getZipEntryMime(file), getZipEntryContent(file));
            }
        }

        private void saveZipEntryResult(String fileName, String language, String creatorName, String creationDate,
                                        String lastModification, String mimeType, String content) {

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

        public String getZipEntryLanguage(ZipEntry file) throws SAXException, IOException {

            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();

            try {
                if (!file.isDirectory()) {
                    InputStream content = zf.getInputStream(file);
                    //Parsing the given document
                    parser.parse(content, handler, metadata, new ParseContext());

                    LanguageIdentifier language = new LanguageIdentifier(handler.toString());
                    //System.out.println(file + " Language name: " + language.getLanguage());
                    return language.getLanguage();
                } else return null;
            } catch (NullPointerException | TikaException e) {
                return null;
            }
        }

        public String getZipEntryCreator(ZipEntry file) throws IOException, TikaException, SAXException {

            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            //System.out.println(file.getName());
            InputStream is = zf.getInputStream(file);

            parser.parse(is, handler, metadata, new ParseContext());
            String creator = metadata.get(Metadata.CREATOR);

            return creator;
        }

        public String getZipEntryCreationDate(ZipEntry file) throws IOException, TikaException, SAXException {

            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            //System.out.println(file.getName());
            InputStream is = zf.getInputStream(file);

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

        public String getZipEntryLastModification(ZipEntry file) throws IOException, TikaException, SAXException {

            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            //System.out.println(file.getName());
            InputStream is = zf.getInputStream(file);

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

        public String getZipEntryMime(ZipEntry file) throws IOException, TikaException, SAXException {
//            Tika tika = new Tika();
//            String mimeType = tika.detect(file);
            return null;
        }

        public String getZipEntryContent(ZipEntry file) throws IOException {
            BodyContentHandler handler = new BodyContentHandler();
            AutoDetectParser parser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            InputStream is = zf.getInputStream(file);
            try {
                parser.parse(is, handler, metadata);
                String text = handler.toString();
                return text;
            } catch (SAXException | IOException | TikaException e) {
                e.printStackTrace();
            } finally {
                is.close();
            }
            return null;
        }

    }
}
