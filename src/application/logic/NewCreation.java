package application.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewCreation {
    private static String _searchTerm;
    private String _selectedChunk;

    public NewCreation() {
        _selectedChunk = null;
    }

    // This method will clean the chunks folder if the creation is cancelled.
    public void cleanChunks() {
        File chunksFolder = Folders.CHUNKS.asFolder();
        if (chunksFolder.exists()) {
            for (final File chunkFileName : chunksFolder.listFiles()) {
                chunkFileName.delete();
            }
            chunksFolder.delete();
        }
    }

    public int numberOfWords(String chunk) {
        if (chunk.isEmpty()) {
            return 0;
        }

        // Splits the input at any instance of one or more whitespace character
        // The number of splits is the number of words
        String[] words = chunk.split("\\s+");
        return words.length;
    }

    public List<String> getChunkNamesList() {
        // The chunks directory where all chunks are stored.
        File chunksFolder = Folders.CHUNKS.asFolder();
        if (!chunksFolder.exists()) {
            chunksFolder.mkdirs();
        }

        List<String> chunkNamesList = new ArrayList<String>();
        // Checks every file in the chunks directory. If a file is .wav format,
        // adds the file name without .wav extension to the list of chunkNames
        for (File fileName : chunksFolder.listFiles()) {
            if (fileName.getName().endsWith(".wav")) {
                chunkNamesList.add(fileName.getName().replace(".wav", ""));
            }
        }

        return chunkNamesList;
    }

    public void setSearchTerm(String searchTerm) {
        _searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return _searchTerm;
    }

    public void setSelectedChunk(String selectedChunk) {
        _selectedChunk = selectedChunk;
    }

    public String getSelectedChunk() {
        return _selectedChunk;
    }
}
