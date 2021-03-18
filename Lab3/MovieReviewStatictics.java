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
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MovieReviewStatictics {
    private static final String DOCUMENTS_PATH = "movies/";
    private int _verbCount = 0;
    private int _nounCount = 0;
    private int _adjectiveCount = 0;
    private int _adverbCount = 0;
    private int _totalTokensCount = 0;

    private PrintStream _statisticsWriter;

    private SentenceModel _sentenceModel;
    private TokenizerModel _tokenizerModel;
    private DictionaryLemmatizer _lemmatizer;
    private PorterStemmer _stemmer;
    private POSModel _posModel;
    private TokenNameFinderModel _peopleModel;
    private TokenNameFinderModel _locationsModel;
    private TokenNameFinderModel _organizationsModel;

    public static void main(String[] args) {
        MovieReviewStatictics statictics = new MovieReviewStatictics();
        statictics.run();
    }

    private void run() {
        try {
            initModelsStemmerLemmatizer();

            File dir = new File(DOCUMENTS_PATH);
            File[] reviews = dir.listFiles((d, name) -> name.endsWith(".txt"));

            _statisticsWriter = new PrintStream("statistics.txt", "UTF-8");

            Arrays.sort(reviews, Comparator.comparing(File::getName));
            for (File file : reviews) {
                System.out.println("Movie: " + file.getName().replace(".txt", ""));
                _statisticsWriter.println("Movie: " + file.getName().replace(".txt", ""));

                String text = new String(Files.readAllBytes(file.toPath()));
                processFile(text);

                _statisticsWriter.println();
            }

            overallStatistics();
            _statisticsWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initModelsStemmerLemmatizer() {
        //try
        //{
        // TODO: load all OpenNLP models (+Porter stemmer + lemmatizer)
        // from files (use class variables)
        try {
            File sentenceModelFile = new File(OpenNLP.SENTENCE_MODEL);
            File tokenModelFile = new File(OpenNLP.TOKENIZER_MODEL);
            File lemmModelFile = new File(OpenNLP.LEMMATIZER_DICT);
            File posModelFile = new File(OpenNLP.POS_MODEL);
            File peopleModelFile = new File(OpenNLP.PERSON_NAME_MODEL);
            File locationModelFile = new File(OpenNLP.LOCATION_NAME_MODEL);
            File organizationModelFile = new File(OpenNLP.ORGANIZATION_NAME_MODEL);

            _sentenceModel = new SentenceModel(sentenceModelFile);
            _tokenizerModel = new TokenizerModel(tokenModelFile);
            _stemmer = new PorterStemmer();
            _lemmatizer = new DictionaryLemmatizer(lemmModelFile);
            _posModel = new POSModel(posModelFile);
            _peopleModel = new TokenNameFinderModel(peopleModelFile);
            _locationsModel = new TokenNameFinderModel(locationModelFile);
            _organizationsModel = new TokenNameFinderModel(organizationModelFile);


        } catch (IOException ex) {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFile(String text) {
        // TODO: process the text to find the following statistics:
        // For each movie derive:
        //    - number of sentences
        // TODO derive sentences (update noSentences variable)
        SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(_sentenceModel);
        int noSentences = sentenceDetectorME.sentDetect(text).length;

        //    - number of tokens
        // TODO derive tokens and POS tags from text
        // (update noTokens and _totalTokensCount)
        TokenizerME tokenizerME = new TokenizerME(_tokenizerModel);
        String[] tokens = tokenizerME.tokenize(text);
        int noTokens = tokens.length;

        //    - number of (unique) stemmed forms
        // TODO perform stemming (use derived tokens)
        // (update noStemmed)
        ArrayList<String> stemSentence = new ArrayList<>();
        for (String s: tokens){
            String stm = _stemmer.stem(s);
            stm = stm.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (!stemSentence.contains(stm) && !stm.equals("")){
                stemSentence.add(stm);
            }
        }
        int noStemmed = stemSentence.size();

        //    - number of (unique) words from a dictionary (lemmatization)
        // TODO perform lemmatization (use derived tokens)
        POSTaggerME posTaggerME = new POSTaggerME(_posModel);
        String[] tags = posTaggerME.tag(tokens);
        String[] lemms = _lemmatizer.lemmatize(tokens, tags);
        ArrayList<String> lemms_fin = new ArrayList<>();
        for (String s: lemms){
            if (!lemms_fin.contains(s) && !s.equals("O")){
                lemms_fin.add(s);
            }
        }
        int noWords = lemms_fin.size();

        // TODO derive people, locations, organisations (use tokens),
        //    -  people
        NameFinderME peopleME = new NameFinderME(_peopleModel);
        Span[] people = peopleME.find(tokens);
        String spanPeople = Arrays.toString(Span.spansToStrings(people, tokens));
        spanPeople = spanPeople.substring(1, spanPeople.length() - 1);
        String[] tmpPeople = spanPeople.split(",");
        ArrayList<String> people_list = new ArrayList<>();
        for (String name : tmpPeople) {
            name = name.trim();
            people_list.add(name);
        }
        //    - locations
        NameFinderME locationsME = new NameFinderME(_locationsModel);
        Span[] locations = locationsME.find(tokens);
        String spanLocations = Arrays.toString(Span.spansToStrings(locations, tokens));
        spanLocations = spanLocations.substring(1, spanLocations.length() - 1);
        String[] tmpLocations = spanLocations.split(",");
        ArrayList<String> locations_list = new ArrayList<>();
        for (String name : tmpLocations) {
            name = name.trim();
            locations_list.add(name);
        }
        //    - organisations
        NameFinderME organisationsME = new NameFinderME(_organizationsModel);
        Span[] organisations = organisationsME.find(tokens);
        String spanOrganisations = Arrays.toString(Span.spansToStrings(organisations, tokens));
        spanOrganisations = spanOrganisations.substring(1, spanOrganisations.length() - 1);
        String[] tmpOrganizations = spanOrganisations.split(",");
        ArrayList<String> organizations_list = new ArrayList<>();
        for (String name : tmpOrganizations) {
            name = name.trim();
            organizations_list.add(name);
        }

        // TODO + compute the following overall (for all movies) POS tagging statistics:
        //    - percentage number of adverbs (class variable, private int _verbCount = 0)
        //    - percentage number of adjectives (class variable, private int _nounCount = 0)
        //    - percentage number of verbs (class variable, private int _adjectiveCount = 0)
        //    - percentage number of nouns (class variable, private int _adverbCount = 0)
        //    + update _totalTokensCount

        // ------------------------------------------------------------------

        // TODO update overall statistics - use tags and check first letters
        // (see https://www.clips.uantwerpen.be/pages/mbsp-tags; first letter = "V" = verb?)

        // ------------------------------------------------------------------

        saveResults("Sentences", noSentences);
        saveResults("Tokens", noTokens);
        saveResults("Stemmed forms (unique)", noStemmed);
        saveResults("Words from a dictionary (unique)", noWords);

        saveList("People", people_list);
        saveList("Locations", locations_list);
        saveList("Organizations", organizations_list);
    }


    private void saveResults(String feature, int count) {
        String s = feature + ": " + count;
        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void saveNamedEntities(String entityType, Span spans[], String tokens[]) {
        StringBuilder s = new StringBuilder(entityType + ": ");
        for (int sp = 0; sp < spans.length; sp++) {
            for (int i = spans[sp].getStart(); i < spans[sp].getEnd(); i++) {
                s.append(tokens[i]);
                if (i < spans[sp].getEnd() - 1) s.append(" ");
            }
            if (sp < spans.length - 1) s.append(", ");
        }

        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void saveList(String entityType, ArrayList<String> people_list) {
        StringBuilder s = new StringBuilder(entityType + ": ");
        int c = 0;
        for (String name: people_list){
            if (c < people_list.size() - 1){
                s.append(name).append(", ");
            } else {
                s.append(name);
            }
            c++;
        }

        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void overallStatistics() {
        _statisticsWriter.println("---------OVERALL STATISTICS----------");
        DecimalFormat f = new DecimalFormat("#0.00");

        if (_totalTokensCount == 0) _totalTokensCount = 1;
        String verbs = f.format(((double) _verbCount * 100) / _totalTokensCount);
        String nouns = f.format(((double) _nounCount * 100) / _totalTokensCount);
        String adjectives = f.format(((double) _adjectiveCount * 100) / _totalTokensCount);
        String adverbs = f.format(((double) _adverbCount * 100) / _totalTokensCount);

        _statisticsWriter.println("Verbs: " + verbs + "%");
        _statisticsWriter.println("Nouns: " + nouns + "%");
        _statisticsWriter.println("Adjectives: " + adjectives + "%");
        _statisticsWriter.println("Adverbs: " + adverbs + "%");

        System.out.println("---------OVERALL STATISTICS----------");
        System.out.println("Adverbs: " + adverbs + "%");
        System.out.println("Adjectives: " + adjectives + "%");
        System.out.println("Verbs: " + verbs + "%");
        System.out.println("Nouns: " + nouns + "%");
    }

}
