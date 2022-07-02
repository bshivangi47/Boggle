import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Boggle : Solve the boggle puzzle
 * @author : Shivangi Bhatt
 * Reference : https://www.geeksforgeeks.org/boggle-find-possible-words-board-characters/
 */
public class Boggle {

    //define the class variables
    List<String> dictionary;
    List<List<Character>> puzzle;
    List<String> detectedWords;
    public final String[][] directions;
    private int puzzleWidth;
    private int puzzleHeight;
    private int startX;
    private int startY;
    private boolean isPuzzleReady;

    /*
     * Constructor : City
     * method purpose : Initialize the variables of object
     * arguments : none
     */
    Boggle() {
        dictionary = new ArrayList();
        puzzle = new ArrayList();
        detectedWords = new ArrayList<>();
        directions = new String[][]{{"N", "U", "E"}, {"L", "X", "R"}, {"W", "D", "S"}};
        puzzleWidth = 0;
        puzzleHeight = 0;
        startX = 0;
        startY = 0;
    }

    /*
     * method name : getDictionary
     * method purpose : stores the dictionary from the stream
     * arguments : BufferedReader stream
     * return value : returns true if all the words are stored in the dictionary and false otherwise
     */
    boolean getDictionary(BufferedReader stream) throws IOException {
        //Check if stream is null
        if (stream == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        //Define string line
        String line;

        //Read the BufferedReader stream line by line until end of line or blank line is encountered
        while ((line = stream.readLine()) != null && (line.length()) != 0) {
            // Check if length of line is less than 2
            if (line.length() < 2) {
                // if true, return false and empty the dictionary
                dictionary.clear();
                return false;
            }
            //add the line to the dictionary
            dictionary.add(line);
        }
        //return true
        return true;
    }

    /*
     * method name : getPuzzle
     * method purpose : stores the puzzle from the  given stream
     * arguments : BufferedReader stream
     * return value : returns true if the puzzle is stored and false otherwise
     */
    boolean getPuzzle(BufferedReader stream) throws IOException {

        //Check if stream is null
        if (stream == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        //Define string line
        String line;
        //initialize the incrementer
        int i = 0;

        //Read stream - line by line until end of line or blank line is encountered
        while ((line = stream.readLine()) != null && (line.length()) != 0) {
            //convert each string line to list of characters
            List<Character> charactersList =
                    line.chars()
                            .mapToObj(e -> (char) e)
                            .collect(Collectors.toList());
            //check if incrementer is zero
            if (i == 0) {
                // if yes, set the width of the puzzle to length of the line
                puzzleWidth = line.length();
                // add the charactersList to the puzzle
                puzzle.add(charactersList);
            }
            //check if length of line is not equal to puzzleWidth
            else if (line.length() != puzzleWidth) {
                //If true throw an exception and set variable puzzle
                isPuzzleReady = false;
                throw new IllegalArgumentException("Invalid input");
            } else {
                // add the charactersList to the puzzle
                puzzle.add(charactersList);
            }
            //Increment the incrementer variable
            i++;
        }
        //Mark the puzzle as ready
        isPuzzleReady = true;
        //set the puzzleHeight
        puzzleHeight = puzzle.size();
        //return true
        return true;
    }


    /*
     * method name : solve
     * method purpose : solve the puzzle to find the words that exist in the dictionary
     * arguments : none
     * return value : returns list of defined words of type string
     */
    List<String> solve() throws Exception {

        //Check if puzzle is ready
        if (!isPuzzleReady) {
            throw new Exception("Puzzle is not ready");
        }

        // Define visited array that tracks what characters are visited
        boolean[][] visited = new boolean[puzzleHeight][puzzleWidth];

        // Initialize found word and the path empty string
        String foundWord = "";
        String path = "";

        //Loop through every character to find words start with that character
        for (int i = 0; i < puzzleHeight; i++)
            for (int j = 0; j < puzzleWidth; j++)
                solveRecursive(visited, i, j, foundWord, path, "");

        // Sort the list of detected words alphabetically
        this.detectedWords.sort(String::compareToIgnoreCase);
        //return detected words
        return this.detectedWords;
    }


    /*
     * method name : solveRecursive
     * method purpose : solve the puzzle recursively to find the words that exist in the dictionary
     * arguments : puzzle
     * return value : returns list of defined words of type string
     */
    private void solveRecursive(boolean[][] visited, int row, int col, String wordFound, String path, String newDirection) {
        // Mark current cell as visited
        visited[row][col] = true;

        // append current character to wordFound
        List<Character> puzzleRow = puzzle.get(row);
        wordFound = wordFound + puzzleRow.get(col);

        // If length of word found is 1
        if (wordFound.length() == 1) {
            // Set the start X and Y coordinate
            startX = col;
            startY = row;
        }

        // append current newDirection to path
        path += newDirection;

        // Check if word is present in dictionary, then add it to the list of detected words
        if (isWordInDictionary(wordFound)) {
            // Get the revised X and Y coordinates based on the lower left coordinate as (1,1)
            int X = startX + 1;
            int Y = puzzleHeight - startY;

            //Store the final output String
            String outputString = wordFound + "\t" + X + "\t" + Y + "\t" + path;

            //Mark that the word is not already present in the list of detected words
            boolean isWordNotFound = true;

            //Loop through list of detected words to check if the word is present
            for (String detectedWord :
                    this.detectedWords) {
                // Split the detected words to array of string
                String[] detectedWordInfo = detectedWord.split("\\t+");
                //Get the word
                String word = detectedWordInfo[0];
                //Check if the word is equal to the word found
                if (word.equalsIgnoreCase(wordFound)) {
                    // If true, set the isWordNotFound to false
                    isWordNotFound = false;

                    //Store the x and y coordinate of detected word
                    int detectedWordXCoordinate = Integer.parseInt(detectedWordInfo[1]);
                    int detectedWordYCoordinate = Integer.parseInt(detectedWordInfo[2]);

                    //check if the X coordinate of current word is less than the X coordinate of detect word
                    if (X < detectedWordXCoordinate) {
                        // If true, replace the word in the list of detected words and break out of the loop
                        detectedWords.set(detectedWords.indexOf(detectedWord), outputString);
                        break;

                    }
                    //Check if X coordinate of current word is less than the X coordinate of detect word
                    else if (X == detectedWordXCoordinate) {

                        //check if the Y coordinate of current word is less than the Y coordinate of detect word

                        if (Y < detectedWordYCoordinate) {
                            // If true, replace the word in the list of detected words and break out of the loop
                            detectedWords.set(detectedWords.indexOf(detectedWord), outputString);
                            break;
                        }


                    }
                }
            }
            //If word is not found, add it to the list of detected words
            if (isWordNotFound) {
                detectedWords.add(wordFound + "\t" + X + "\t" + Y + "\t" + path);
            }

        }

        //Check if the the word in a dictionary exists that starts with wordFound
        if (wordStartsWithExists(wordFound)) {
            // If true, only then traverse 8 adjacent cells of current cell
            for (int i = row - 1; i <= row + 1 && i < puzzleHeight; i++)
                for (int j = col - 1; j <= col + 1 && j < puzzleWidth; j++)
                    //Check if the cell is not already visited
                    if (i >= 0 && j >= 0 && !visited[i][j]) {
                        //Get the direction of the adjacent cell from the current cell
                        String newDir = getDirection(i, j, row, col);
                        //solve recursively
                        solveRecursive(visited, i, j, wordFound, path, newDir);
                    }
        }

        //Remove the current character from the wordFound and set the current cell's visited state to false.
         wordFound = "" + wordFound.charAt(wordFound.length() - 1);
        visited[row][col] = false;


    }


    /*
     * method name : print
     * method purpose : Returns the string to print the puzzle
     * arguments : none
     * return value : returns string of the puzzle
     */
    String print() {
        //Initialize the output string to empty string
        String outputString = "";
        //Loop through all the characters in the puzzle
        for (List<Character> listOfCharacter : puzzle) {
            for (Character character :
                    listOfCharacter) {
                //Append the character to the outputString
                outputString += character;
            }
            //If the listOfCharacter is not on the last index of the puzzle
            if (listOfCharacter != puzzle.get(puzzle.size() - 1)) {
                //Apped the new line character to the outputString
                outputString += "\n";
            }
        }

        return outputString;
    }


    /*
     * method name : isWordInDictionary
     * method purpose : To check ifa word exist in the dictionary
     * arguments : String word
     * return value : returns true if the word is present in the dictionary and false otherwise
     */
    private boolean isWordInDictionary(String word) {
        return dictionary.stream().anyMatch(word::equalsIgnoreCase);
    }


    /*
     * method name : isSubset
     * method purpose : check if the word in the dictionary exists that starts with the given word
     * arguments : String word
     * return value : returns true if the word is present in the dictionary and false otherwise
     */
    private boolean wordStartsWithExists(String word) {
        for (String words : dictionary) {
            if (word.length() <= words.length() && words.startsWith(word)) {
                return true;
            }
        }
        return false;
    }


    /*
     * method name : solve
     * method purpose : solve the puzzle to find the words that exist in the dictionary
     * arguments : none
     * return value : returns list of defined words of type string
     */
    private String getDirection(int row, int col, int i, int j) {
        return directions[row - i + 1][col - j + 1];
    }

}
