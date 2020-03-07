package application.tasks;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WikiSearchTask extends Task<String> {

    private String _searchTerm;

    public WikiSearchTask(String searchTerm) {
        _searchTerm = searchTerm;
    }

    @Override
    protected String call() {

        String searchResult = "";
        try {
            String command = "wikit " + _searchTerm;
            ProcessBuilder builder = new ProcessBuilder(new String[]{"/bin/bash", "-c", command});
            Process process = builder.start();

            InputStream stdout = process.getInputStream();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

            String line;
            while ((line = stdoutBuffered.readLine()) != null) {
                searchResult += line;
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateProgress(0, 10);
        String trimmedSearchResult = searchResult.trim();
        String wikiSearchOutput = trimmedSearchResult.replace(". ", ".\n\n");

        return wikiSearchOutput.trim();
    }
}
