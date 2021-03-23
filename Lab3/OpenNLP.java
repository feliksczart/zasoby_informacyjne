//ODPOWIEDZI:
//  e1 2d - the probability decreases
//  e1 3c - outcomes are different because of the different methods of writing numbers
//  e1 3e - there are small differences in tokens probability which in both models have value less than 1.0
//  e1 4d - yes, very short sentences like "Hi.", aren't taken as a sentence
//  e1 4f - punctuation or question marks cause sentences of medium length to be treated
//          as separate sentences which without them are not sufficient to be treated as a sentence
//  e1 5c - the "like" in the first sentence should be a verb rather than a conjunction as shown in the array
//  e1 6b - "are" is discarded by the lemmatizer, it also replaces words such as "OpenNLP" with 0
//          pos tags help in putting words into correct categories??????????????????
//  e1 7a - post tags help with the program's inability to see context thus providing basic sentence structure????????????????????????????????????
//  e1 7b - B is the start of a chunk, while I means it's continuation
//  the result seems to be correct?????????????
//  e1 8b - the results are correct
//  e2 2 -  descriptions in general are going to contain a lot more adjectives due to their descriptive nature as shown by the results of this exercise
//          there are more parts of speech?????????????????




// A maximum entropy (ME) model is used to evaluate end-of-sentence characters in a string

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OpenNLP {

    public static String LANG_DETECT_MODEL = "models/langdetect-183.bin";
    public static String TOKENIZER_MODEL = "models/en-token.bin";
    public static String DE_TOKENIZER_MODEL = "models/de-token.bin";
    public static String SENTENCE_MODEL = "models/en-sent.bin";
    public static String POS_MODEL = "models/en-pos-maxent.bin";
    public static String CHUNKER_MODEL = "models/en-chunker.bin";
    public static String LEMMATIZER_DICT = "models/en-lemmatizer.dict";
    public static String PERSON_NAME_MODEL = "models/en-ner-person.bin";
    public static String LOCATION_NAME_MODEL = "models/en-ner-location.bin";
    public static String ORGANIZATION_NAME_MODEL = "models/en-ner-organization.bin";
    public static String ENTITY_XYZ_MODEL = "models/en-ner-xyz.bin";

    public static void main(String[] args) throws IOException {
        OpenNLP openNLP = new OpenNLP();
        openNLP.run();
    }

    public void run() throws IOException {

        // languageDetection();
        // tokenization();
        // sentenceDetection();
        // posTagging();
//        lemmatization();
//        stemming();
//         chunking();
         nameFinding();
    }

    private void languageDetection() throws IOException {
        File modelFile = new File(LANG_DETECT_MODEL);
        LanguageDetectorModel model = new LanguageDetectorModel(modelFile);
        LanguageDetectorME modelME = new LanguageDetectorME(model);

        String text = "";
        String textMixed = "";
        //text = "cats";
//		 text = "cats like milk";
//		 text = "Many cats like milk because in some ways it reminds them of their mother's milk.";
        // text = "The two things are not really related. Many cats like milk because in
        // some ways it reminds them of their mother's milk.";

        text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk. "
                + "It is rich in fat and protein. They like the taste. They like the consistency . "
                + "The issue as far as it being bad for them is the fact that cats often have difficulty digesting milk and so it may give them "
                + "digestive upset like diarrhea, bloating and gas. After all, cow's milk is meant for baby calves, not cats. "
                + "It is a fortunate quirk of nature that human digestive systems can also digest cow's milk. But humans and cats are not cows.";

        textMixed = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk. "
                + "It is rich in fat and protein. They like the taste. They like the consistency . "
                + "The issue as far as it being bad for them is the fact that cats often have difficulty digesting milk and so it may give them "
                + "digestive upset like diarrhea, bloating and gas. After all, cow's milk is meant for baby calves, not cats. "
                + "Der Normalfall ist allerdings der, dass Salonl�wen Milch weder brauchen " +
                "noch gut verdauen k�nnen.";
        // text = "Many cats like milk because in some ways it reminds them of their
        // mother's milk. Le lait n'est pas forc�ment mauvais pour les chats";
//        text = "Many cats like milk because in some ways it reminds them of their " +
//                "mother's milk. Le lait n'est pas forc�ment mauvais pour les chats. "
//                + "Der Normalfall ist allerdings der, dass Salonl�wen Milch weder brauchen " +
//                "noch gut verdauen k�nnen.";

        System.out.println(modelME.predictLanguage(text));
        System.out.println(modelME.predictLanguage(textMixed));
    }

    private void tokenization() throws IOException {
        File modelFile = new File(TOKENIZER_MODEL);
        TokenizerModel model = new TokenizerModel(modelFile);
        TokenizerME modelME = new TokenizerME(model);

        File deModelFile = new File(DE_TOKENIZER_MODEL);
        TokenizerModel deModel = new TokenizerModel(deModelFile);
        TokenizerME deModelME = new TokenizerME(deModel);

        String text = "";

//        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
//                + "but there may have been instances of domestication as early as the Neolithic from around 9500 years ago (7500 BC).";
//		text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
//				+ "but there may have been instances of domestication as early as the Neolithic from around 9,500 years ago (7,500 BC).";
        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9 500 years ago ( 7 500 BC).";

        System.out.println(Arrays.toString(modelME.tokenize(text)));
        System.out.println(Arrays.toString(modelME.getTokenProbabilities()));

        if (false) {
            int c = 0;
            for (double i : modelME.getTokenProbabilities()) {
                if (i != 1.0) {
                    System.out.println(modelME.tokenize(text)[c]);
                }
                c++;
            }
        }

        System.out.println(Arrays.toString(deModelME.tokenize(text)));
        System.out.println(Arrays.toString(deModelME.getTokenProbabilities()));


    }

    private String[] tokenization(String text) throws IOException {
        File modelFile = new File(TOKENIZER_MODEL);
        TokenizerModel model = new TokenizerModel(modelFile);
        TokenizerME modelME = new TokenizerME(model);

        File deModelFile = new File(DE_TOKENIZER_MODEL);
        TokenizerModel deModel = new TokenizerModel(deModelFile);
        TokenizerME deModelME = new TokenizerME(deModel);

        return modelME.tokenize(text);
    }

    private void sentenceDetection() throws IOException {
        File modelFile = new File(SENTENCE_MODEL);
        SentenceModel model = new SentenceModel(modelFile);
        SentenceDetectorME modelME = new SentenceDetectorME(model);

        String text = "";
        text = "Hi. How are you? Welcome to OpenNLP. "
                + "We provide multiple built-in methods for Natural Language Processing.";
//		text = "Hi. How are you?! Welcome to OpenNLP? "
//				+ "We provide multiple built-in methods for Natural Language Processing.";
//		text = "Hi. How are you? Welcome to OpenNLP.?? "
//				+ "We provide multiple . built-in methods for Natural Language Processing.";
//		text = "The interrobang, also known as the interabang (often represented by ?! or !?), "
//				+ "is a nonstandard punctuation mark used in various written languages. "
//				+ "It is intended to combine the functions of the question mark (?), or interrogative point, "
//				+ "and the exclamation mark (!), or exclamation point, known in the jargon of printers and programmers as a \"bang\". ";

        System.out.println(Arrays.toString(modelME.sentDetect(text)));
        System.out.println(Arrays.toString(modelME.getSentenceProbabilities()));
    }

    private void posTagging() throws IOException {
        File modelFile = new File(POS_MODEL);
        POSModel model = new POSModel(modelFile);
        POSTaggerME modelME = new POSTaggerME(model);

        String[] sentence = new String[0];
//        sentence = new String[]{"Cats", "like", "milk"};
		sentence = new String[]{"Cat", "is", "white", "like", "milk"};
//		sentence = new String[] { "Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
//				"built-in", "methods", "for", "Natural", "Language", "Processing" };
//		sentence = new String[] { "She", "put", "the", "big", "knives", "on", "the", "table" };

        System.out.println(Arrays.toString(modelME.tag(sentence)));
    }

    private void lemmatization() throws IOException {
        File modelFile = new File(LEMMATIZER_DICT);
        DictionaryLemmatizer dict = new DictionaryLemmatizer(modelFile);

        String[] text = new String[0];
        text = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};
        String[] tags = new String[0];
        tags = new String[]{"NNP", "WRB", "VBP", "PRP", "VB", "TO", "VB", "PRP", "VB", "JJ", "JJ", "NNS", "IN", "JJ",
                "NN", "VBG"};

        System.out.println(Arrays.toString(dict.lemmatize(text, tags)));

    }

    private void stemming() {
        PorterStemmer porterStemmer = new PorterStemmer();

        ArrayList<String> stemSentence = new ArrayList<>();
        String[] sentence = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};

        for (String str: sentence){
            stemSentence.add(porterStemmer.stem(str));
        }

        System.out.println(Arrays.toString(sentence));
        System.out.println(stemSentence);

    }

    private void chunking() throws IOException {
        File modelFile = new File(CHUNKER_MODEL);
        ChunkerModel model = new ChunkerModel(modelFile);
        ChunkerME modelME = new ChunkerME(model);

        String[] sentence = new String[]{"She", "put", "the", "big", "knives", "on", "the", "table"};
        String[] tags = new String[]{"PRP", "VBD", "DT", "JJ", "NNS", "IN", "DT", "NN"};

        System.out.println(Arrays.toString(modelME.chunk(sentence, tags)));
    }

    private void nameFinding() throws IOException {
        File modelFile = new File(PERSON_NAME_MODEL);
        TokenNameFinderModel model = new TokenNameFinderModel(modelFile);
        NameFinderME modelME = new NameFinderME(model);

        File xyzModelFile = new File(ENTITY_XYZ_MODEL);
        TokenNameFinderModel xyzModel = new TokenNameFinderModel(xyzModelFile);
        NameFinderME xyzModelME = new NameFinderME(xyzModel);

        String text = "he idea of using computers to search for relevant pieces of information was popularized in the article "
                + "As We May Think by Vannevar Bush in 1945. It would appear that Bush was inspired by patents "
                + "for a 'statistical machine' - filed by Emanuel Goldberg in the 1920s and '30s - that searched for documents stored on film. "
                + "The first description of a computer searching for information was described by Holmstrom in 1948, "
                + "detailing an early mention of the Univac computer. Automated information retrieval systems were introduced in the 1950s: "
                + "one even featured in the 1957 romantic comedy, Desk Set. In the 1960s, the first large information retrieval research group "
                + "was formed by Gerard Salton at Cornell. By the 1970s several different retrieval techniques had been shown to perform "
                + "well on small text corpora such as the Cranfield collection (several thousand documents). Large-scale retrieval systems, "
                + "such as the Lockheed Dialog system, came into use early in the 1970s.";

        String[] textArray = tokenization(text);
        System.out.println(Arrays.toString(textArray));
        System.out.println(Arrays.toString(modelME.find(textArray)));
        System.out.println(Arrays.toString(xyzModelME.find(textArray)));
    }
}
