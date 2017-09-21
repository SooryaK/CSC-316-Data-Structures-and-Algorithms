import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * @file proj1.java
 * @author Soorya Kumar
 *
 * CSC 316 Project 1
 * Compresses and Decompresses Files using Move to Front algorithm
 * 
 * This program takes a text input file (redirected from standard input)
 * and and compresses it into an output file. The compression is accomplished
 * by replacing common repeated words with a number throughout the file. 
 * A word list is created to keep track of every word found in the input file.
 * When a word is found it is added to the front of the word list, if a word 
 * is already in the list it is brought to the front of the list. The first
 * instance of a word is always kept untouched in the compressed output. 
 * An time a word is repeated in the input, meaning it is already in the word list,
 * it is replaced with it's current index in the word list (condensing the word into just
 * a number). 
 * 
 * In this way the process can be reversed during decompression. A compressed file can be 
 * taken and the wordlist can be fully reconstructed because the first instance of
 * every word is still available in the output file of compression. The numbers in the 
 * compressed file will match with the order of the first instances of the words that are
 * entered into the word list. Repeated words are moved to the front of the word list
 * even with a compressed file as well so the entire compressed file can be restored 
 * to be the same as the original uncompressed version.
 *
 * To compile: javac proj1.java
 *
 * To run: java proj 1 < inputfile > outputfile
 */
public class proj1 {
    /** Whether the file should be compressed */
    public static boolean toCompress;
    /** number of uncompressed characters from input file */
    public static int uncompressedCharacters = 0;
    /** number of compressed characters in compressed file */
    public static int compressedCharacters = 0;
    /** LinkedList to track all original words in input */
    public static LinkedList<String> wordList = new LinkedList<String>();

    /**
     * Simple algorithm to move a word/number representing word
     * repeated in input file to the front of the wordList LinkedList
     *
     * @param index index of word
     * @return updated LinkedList
     */
    public static LinkedList<String> moveToFront(int index) {
        wordList.add(0, wordList.remove(index));
        return wordList;
    }

    /**
     * Helper function to get a queue of punctuation and whitespace
     * from a line from the input file to reinsert after all tokens
     * of the input line have been processed
     *
     * @param s string, should be one line of text from input
     * @return queue of punctuation and whitespace
     */
    public static Queue<String> getPunctuationAndWhiteSpace(String s) {
        Queue<String> q = new LinkedList<String>();
        int index = 0;
        while (index < s.length()) {
            if (!Character.isLetterOrDigit(s.charAt(index))) {
                String puncAndWhiteSpace = "";
                while (index < s.length() && !Character.isLetterOrDigit(s.charAt(index))) {
                    puncAndWhiteSpace = puncAndWhiteSpace.concat("" + s.charAt(index));
                    index++;
                }
                q.add(puncAndWhiteSpace);
            }    
            else {
                index++;
            }
        }
        return q;
    }

    /**
     * Helper Function to process one line from the input, handles differently
     * if file needs to be compressed or decompressed
     *
     * @param s string, should be a single line from input
     * @return processed line, either compressed or decompressed
     */
    public static String processLine(String s) {
        if (s.equals("")) 
            return "";
        String processedLine = "";
        Queue<String> puncAndWhiteSpace = getPunctuationAndWhiteSpace(s);
        if (!Character.isLetterOrDigit(s.charAt(0))) {
            processedLine = processedLine.concat(puncAndWhiteSpace.remove());
        }
        Scanner lineScanner = new Scanner(s.replaceAll("[™\"\\(\\).,/%&!@#$?_><;:'*~…®=\\^\\+\\-\\–\\—\\[\\] && [^A-Z]]", " "));
        try {
            while (lineScanner.hasNext()) {
                if (toCompress) {
                    String currentWord = lineScanner.next();
                    if (wordList.contains(currentWord)) {
                        processedLine = processedLine.concat("" + (wordList.indexOf(currentWord) + 1));
                        moveToFront(wordList.indexOf(currentWord));
                        processedLine = processedLine.concat(puncAndWhiteSpace.poll());
                    }
                    else {
                        processedLine = processedLine.concat(currentWord);
                        wordList.add(0, currentWord);
                        processedLine = processedLine.concat(puncAndWhiteSpace.poll());
                    }
                }
                else {
                    if (lineScanner.hasNextInt()) {
                        int indexOfWord = lineScanner.nextInt();
                        processedLine = processedLine.concat(wordList.get(indexOfWord - 1));
                        moveToFront(indexOfWord - 1);
                        processedLine = processedLine.concat(puncAndWhiteSpace.poll());
                    }
                    else {
                        String currentToken = lineScanner.next();
                        processedLine = processedLine.concat(currentToken);
                        wordList.add(0, currentToken);
                        processedLine = processedLine.concat(puncAndWhiteSpace.poll());
                    }
                }
            }
        }
        catch (NullPointerException e) {
            processedLine = processedLine.concat("");
        }
        if (toCompress) {
            uncompressedCharacters += s.length();
            compressedCharacters += processedLine.length();
        }
        lineScanner.close();
        return processedLine;
    }

    /**
     * Handles the first line of input file and determines if need to
     * compress or decompress. Then processes line by line to compress
     * or decompress and prints the result to standard output. Prints out
     * statistics if necessary. 
     *
     * @throws FileNotFoundException
     */
    public static void compressOrDecompress() throws FileNotFoundException {
        Scanner fileScanner = new Scanner(System.in);
        PrintStream outputFile = new PrintStream(System.out);
        
        String firstLine = fileScanner.nextLine();
        if (firstLine.startsWith("0 ")) {
            toCompress = false;
            String s = processLine(firstLine.substring(2));
            outputFile.println(s);
        }
        else {
            toCompress = true;
            String s = processLine(firstLine);
            outputFile.println("0 " + s);
        }

        if (toCompress) {
            while (fileScanner.hasNextLine()) {
                String s = processLine(fileScanner.nextLine());
                outputFile.println(s);
            }
            outputFile.printf("0 Uncompressed: %d bytes;  Compressed: %d bytes", uncompressedCharacters, compressedCharacters);
        }
        else {
            while (fileScanner.hasNextLine()) {
                String currentLine = fileScanner.nextLine();
                if (!currentLine.startsWith("0 ")) {
                    String s = processLine(currentLine);
                    outputFile.println(s);
                }
            }
        }    
        fileScanner.close();
    }

    /**
     * Main Method
     *
     * @param args only arguments should be redirections of standard input and output
     * @throws FileNotFoundException if File not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        compressOrDecompress();
    }
}