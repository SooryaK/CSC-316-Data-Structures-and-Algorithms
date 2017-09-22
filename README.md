# CSC-316-Data-Structures-and-Algorithms
Programming Assignments from CSC 316 Data Structures and Algorithms

Programs are all completed in Java

Project 1
This program uses a simple move-to-front algorithm.

This program takes a text input file (redirected from standard input) and and compresses it into an output file. The compression is accomplished by replacing common repeated words with a number throughout the file. A word list is created to keep track of every word found in the input file. When a word is found it is added to the front of the word list, if a word is already in the list it is brought to the front of the list. The first instance of a word is always kept untouched in the compressed output. An time a word is repeated in the input, meaning it is already in the word list, it is replaced with it's current index in the word list (condensing the word into just
a number). 

Project 2
This project covers trees and tree traversals.

From preorder and postorder traversals of a given tree as input to the program, constructs the tree and returns results on user queries about relationships between nodes in the tree, and prints level order traversal.

Project 3
This project relates to graph theory and minimum spanning trees. It makes use of custom built heap and up tree data structures.

This project takes a list of weighted edges on a given graph, described by their verticies and weight. It enters all the edges into a heap data structure, which effectively sorts them according to weight. It prints the ordered edges. It then performs Kruskal's algorithm using the sorted edges in the heap as input, as well as up trees in order to create the minimum spanning tress for the set of edges. The minimum spanning tree is printed, and then an adjacency list of the edges is created.

Project 4
This project relates to hash tables.

This project is an implementation of a hash table spell check. A text file that represents a dictionary is given as input to this program. The words in the dictionary are put into a custom built hash table, with a custom hash function. A user text file that is to be spell checked is also provided as input to this program. After the hash table of the dictionary has been created, the program goes through the user text file and checks every word to efficiently check if it is in the dictionary. If it is not in the dictionary the program checks if it is a common variation (plural version or has a common adjective or adverb suffix). In the end a report is printed that shows the number of misspelled words as well as efficiency information for how many probes into the hash table where necessary to accomplish the spell checking.
