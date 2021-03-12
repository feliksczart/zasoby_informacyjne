//Odpowiedzi:
//    e1a4 - some of the phone numbers were not found by the method
//        because numbers in xml have wrong tag


import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.PhoneExtractingContentHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Exercise1
{
    public static void main(String[] args) throws IOException, ParserConfigurationException,
            SAXException, TikaException
    {
        Exercise1 e1 = new Exercise1();
        e1.run();
    }

    private void run() throws ParserConfigurationException, SAXException, IOException, TikaException
    {
//        LinkedList <String> phonesByTwoParses = exercise1a();
        System.out.println("Results of the two parses:");
//        printResults(phonesByTwoParses);

        LinkedList <String> phonesByTika = exercise1b();
        System.out.println("Results of Tika:");
        printResults(phonesByTika);
    }


    private LinkedList <String> exercise1a() throws IOException, ParserConfigurationException, SAXException
    {
        System.out.println("Running exercise 1a...");
        LinkedList <String> results = new LinkedList <>();

        // TODO
        ZipFile file = new ZipFile("zips/Exercise1.zip") ;
        Enumeration entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            InputStream stream = file.getInputStream(entry);

            String extension = "";
            int i = entry.getName().lastIndexOf('.');
            if (i > 0) {
                extension = entry.getName().substring(i+1);
            }
            if (extension.equals("pdf")){
                System.out.println("PDF:");
                PDDocument pdDoc = PDDocument.load(stream);
                String pdfStripper = new PDFTextStripper().getText(pdDoc);
//                System.out.println(pdfStripper); //pdf content line
                String patternString = "\\([0-9]{3}\\) ?[0-9-]+";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(pdfStripper);
                while(matcher.find()){
                    String text = matcher.group();
                    System.out.println("  "+text);
                    results.add(text);
                }
            } else if (extension.equals("xml")){
                System.out.println("XML:");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(stream);
                String content = "";
                int index = 0;
                while (true){
                    try {
                        content = doc.getElementsByTagName("Phone").item(index).getTextContent();
                        System.out.println("  "+content);
                        results.add(content);
                        index++;
                    } catch (NullPointerException npe){
                        break;
                    }
                }
            }
        }

        return results;
    }

    private LinkedList <String> exercise1b() throws IOException, TikaException, SAXException
    {
        System.out.println("Running exercise 1b...");
        LinkedList <String> results = new LinkedList <>();
        // TODO

        Parser parser = new AutoDetectParser();
        Metadata md = new Metadata();
        PhoneExtractingContentHandler handler = new PhoneExtractingContentHandler(new BodyContentHandler(), md);
        InputStream stream = null;
        try {
            stream = TikaInputStream.get(new File("zips/Exercise1.zip"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            parser.parse(stream, handler, md, new ParseContext());
        }catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] numbers = md.getValues("phonenumbers");
        for (String number : numbers) {
            results.add(number);
        }
        return results;
    }


    private void printResults(LinkedList <String> results)
    {
        LinkedList <String> parsedResults = new LinkedList <>();
        for (String s2 : results)
        {
            String s1 = s2.replace(" ", "");
            s1 = s1.replace("(", "");
            s1 = s1.replace(")", "");
            s1 = s1.replace("-", "");
            parsedResults.add(s1);
        }

        LinkedList <String> reference = new LinkedList <>(Arrays.asList(_data));
        LinkedList <String> incorrectly = new LinkedList <>();
        LinkedList <String> missed = new LinkedList <>();

        System.out.println("- detected phone numbers = " + parsedResults.size());
        int t = 0;
        for (String s1 : parsedResults)
            if (reference.contains(s1)) t++;
            else incorrectly.add(s1);

        for (String s1 : reference)
            if (!parsedResults.contains(s1))
                missed.add(s1);

        System.out.println("- correctly detected phone numbers = " + t);
        System.out.println("- incorrect detected phone numbers = " + incorrectly.size());
        for (String s : incorrectly)
            System.out.println("    " + s);
        System.out.println("- missed phone numbers = " + missed.size());
        for (String s : missed)
            System.out.println("    " + s);

    }

    private String _data[] =
            {
                    "7256915622",
                    "4289519018",
                    "9402503286",
                    "7785524197",
                    "2041812298",
                    "8142693192",
                    "8726549759",
                    "6615468662",
                    "2048492437",
                    "2922715420",
                    "6427002319",
                    "5415207134",
                    "4114517460",
                    "4196534921",
                    "9154153005",
                    "8666147732",
                    "9237188291",
                    "5603491440",
                    "9571159173",
                    "5482570883",
                    "3509228486",
                    "6478692640",
                    "1235045237",
                    "7683331038",
                    "4451512278",
                    "3168666968",
                    "6683602279",
                    "4155419711",
                    "2355926980",
                    "9278612234",
                    "6732151992",
                    "1297121453",
                    "9945831692",
                    "1074548772",
                    "2742648914",
                    "2116605343",
                    "6948486721",
                    "8692859252",
                    "1126740391",
                    "1765449317",
                    };
}