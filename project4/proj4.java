import java.util.Scanner;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Project 4 Hash Table Spell Checker
 * 
 * The proj4.java file accepts two command line arguments, one the dicitionary file, and one the user file to spell check.
 * While proj4.java does not read from standard input (and instead uses File IO) it does print to standard output.
 *
 * The dictionary file provides a list of correctly spelled words. The entire dictionary is initially fed into a 
 * hashtable. The user file is then scanned and each word is checked with the dictionary hash table. The hash table
 * is used to make probing and finding the words in the dictionary go faster. A few different rules are used to see 
 * if a different version of a word (plural or uses a suffix like "ing", "ly", "ed") which would still be acceptable
 * are also applied. In the end a list of the misspelled words is provided, and summary report of the number of probes
 * per lookup and other info is provided.
 * 
 * Please use as below:
 * 
 * To Compile:
 * javac proj4.java
 * 
 * To run and print output to command line:
 * java proj4 dict.txt fileToSpellCheck.txt
 * 
 * To run and print output to specified file:
 * java proj4 dict.txt fileToSpellCheck.txt > output.txt
 * 
 * Please do not use redirection for input, only for output.
 * 
 * @file proj4.java
 * @author Soorya Kumar
 */
public class proj4 {

    /** hash table that holds the dictionary */
    public static LinkedList<String>[] hashTable;
    /** number of probes for spell check throughout entirety of users input */
    public static int probes;
    /** number of lookups throughout entirety of users input */
    public static int lookups;
    /** number of words in dictionary */
    public static int wordsInDictionary;
    /** total words in user's input */
    public static int totalWords;
    /** number of misspelled words in user's input */
    public static int misspelledWords;

    /**
     * Hash Function
     * 
     * @param s string to generate hash code for
     * @return int hash code for string 
     */
    public static int hashFunction(String s) {
        char c[] = s.toCharArray();
        long sum = 0;
        for (int i = 0; i < c.length; i++) {    
            sum += c[i] * Math.pow(7, c.length - i); 
        }
        return (int) (sum % 12577);
    }

    /**
     * Processes a word if it isn't in the dictionary to begin with
     * @param s word to process
     * @param i which rule to apply to word
     * @return processed word
     */
    public static String processWord(String s, int i) {
        try {
            String modifiedString = s;
            switch (i) {
            case 1: modifiedString = s.toLowerCase();
                    break;
            case 2: s = s.toLowerCase();
                    if (s.substring(s.length() - 2).equals("'s")){
                        modifiedString = s.substring(0, s.length() - 2);
                    }
                    break;
            case 3: s = s.toLowerCase();
                    if (s.substring(s.length() - 1).equals("s")){
                        modifiedString = s.substring(0, s.length() - 1);
                    }
                    break;
            case 4: s = s.toLowerCase();
                    if (s.substring(s.length() - 2).equals("es")){
                        modifiedString = s.substring(0, s.length() - 2);
                    }
                    break;
            case 5: s = s.toLowerCase();
                    if (s.substring(s.length() - 2).equals("ed")){
                        modifiedString = s.substring(0, s.length() - 2);
                    }
                    break;
            case 6: s = s.toLowerCase();
                    if (s.substring(s.length() - 1).equals("d")){
                        modifiedString = s.substring(0, s.length() - 1);
                    }
                    break;
            case 7: s = s.toLowerCase();
                    if (s.substring(s.length() - 2).equals("er")){
                        modifiedString = s.substring(0, s.length() - 2);
                    }
                    break;
            case 8: s = s.toLowerCase();
                    if (s.substring(s.length() - 1).equals("r")){
                        modifiedString = s.substring(0, s.length() - 1);
                    }
                    break;
            case 9: s = s.toLowerCase();
                    if (s.substring(s.length() - 3).equals("ing")){
                        modifiedString = s.substring(0, s.length() - 3);
                    }
                    break;
            case 10:s = s.toLowerCase();
                    if (s.substring(s.length() - 3).equals("ing")){
                        modifiedString = s.substring(0, s.length() - 3) + "e";
                    }        
                    break;
            case 11:s = s.toLowerCase();
                    if (s.substring(s.length() - 2).equals("ly")){
                        modifiedString = s.substring(0, s.length() - 2);
                    }        
                    break;
            }
            return modifiedString;
        }
        catch (StringIndexOutOfBoundsException e) {
            return s;
        }
    }

    /**
     * Main Method
     * @param args command line arguments
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("usage: java proj4 dict.txt fileToSpellCheck.txt");
            System.exit(0);
        }
        probes = 0;
        lookups = 0;
        wordsInDictionary = 0;
        totalWords = 0;
        misspelledWords = 0;
        hashTable = new LinkedList[12577];
        File dictFile = new File(args[0]);
        File userFile = new File(args[1]);
        Scanner dictScanner = null;
        try {
            dictScanner = new Scanner(dictFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find dictionary file");
        }
        while (dictScanner.hasNext()) {
            String s = dictScanner.next();
            int hashCode = hashFunction(s);
            if (hashTable[hashCode] == null) {
                hashTable[hashCode] = new LinkedList<String>();
                hashTable[hashCode].add(s);
            }
            else{
                hashTable[hashCode].add(s);
            }
            wordsInDictionary++;
        }
        Scanner userScanner = null;
        System.out.printf("Misspelled Words:\n");
        try {
            userScanner = new Scanner(userFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find user file for spell checking");
        }
        while (userScanner.hasNext()) {
            String s = userScanner.next();
            //s = s.replaceAll("[,.;:?!*\"\\(\\)]", "");
            String printS = s.replaceAll("[,.;:?!*\"\\(\\)\\[\\]]", "");;
            s = s.replaceAll("[™\"\\(\\).,/%&!@#$?_><;:*~…®=\\^\\+\\-\\–\\—\\[\\]]", "");
            String originalS = s;
            boolean match = false;
            int processWordRule = 1;
            int interations = 1;
            int hashCode = hashFunction(s);
            while (match == false && interations < 13) {
                if (hashTable[hashCode] != null) {
                    int index = 0;
                    while (match == false && index < hashTable[hashCode].size()) {
                        if (s.equals(hashTable[hashCode].get(index))) {
                            match = true;
                        }
                        index++;
                        probes++;
                    }
                }
                if (match == false && processWordRule < 12) {
                    String modifiedS = s;
                    while (s.equals(modifiedS) && processWordRule < 12) {
                        modifiedS = processWord(originalS, processWordRule++);
                    }
                    s = modifiedS;
                    hashCode = hashFunction(s);
                }
                lookups++;
                interations++;
            }
            //word is not in dictionary and is therefore misspelled
            if (match == false) {
                System.out.println(printS);
                misspelledWords++;
            }
            totalWords++;
        }
        System.out.printf("\nWords in Dicitionary: %d\n", wordsInDictionary);
        System.out.printf("Total number of words spell-checked: %d\n", totalWords);
        System.out.printf("Number of misspelled words: %d\n", misspelledWords);
        System.out.printf("Total number of probes during entire spell checking: %d\n", probes);
        double avgProbesPerWord = (double)probes/(double)totalWords;
        System.out.printf("Average number of probes per word: %.2f\n", avgProbesPerWord);
        double avgProbesPerLookup = (double)probes/(double)lookups;
        System.out.printf("Average number of probes per Lookup operation: %.2f\n", avgProbesPerLookup);
    }
}