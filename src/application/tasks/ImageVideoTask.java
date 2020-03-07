package application.tasks;

import application.logic.FileManager;
import application.logic.Folders;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ImageVideoTask extends Task<Void> {
    private String _searchTerm;
    private String _creationName;
    private int _numberOfImages;

    private final String AUDIO_FILE_DIR;
    private final String TEMP_VIDEO_DIR;
    private final String NO_SOUND_VIDEO_DIR;
    private final String VIDEO_OUTPUT_DIR;
    private final String QUIZ_VIDEO_DIR;

    public ImageVideoTask(String searchTerm, String creationName, int numberOfImages) {
        _searchTerm = searchTerm;
        _creationName = creationName;
        _numberOfImages = numberOfImages;

        String creationsFileDir = Folders.CREATIONS.asString();
        String searchTermDir = creationsFileDir + _searchTerm + "/";

        NO_SOUND_VIDEO_DIR = searchTermDir + "noSoundVideo.mp4";
        TEMP_VIDEO_DIR = searchTermDir + "tempVideo.mp4";
        AUDIO_FILE_DIR = searchTermDir + _searchTerm + ".wav";
        VIDEO_OUTPUT_DIR = creationsFileDir + _creationName + ".mp4";
        QUIZ_VIDEO_DIR = Folders.QUIZ.asString() + _searchTerm + ".mp4";
    }

    @Override
    protected Void call() throws Exception {
        // All video creation methods are completed in this task
        videoCreation();
        mergeAudioAndVideo();

        // need to create the quiz vids before cleanning
        quizVideoCreation();

        FileManager.cleanFolders();
        return null;
    }

    // This method will create the no audio .mp4 file
    // It will use the images from flickr to create a slideshow
    // THis slideshow will have the search term as centered text.
    private void videoCreation() throws IOException, InterruptedException {
        // To determine the length of the .wav file for generation of the video.
        String audioLengthCommand = ("soxi -D " + AUDIO_FILE_DIR);
        ProcessBuilder audioLengthBuilder = new ProcessBuilder("bash", "-c", audioLengthCommand);
        Process audioLengthProcess = audioLengthBuilder.start();
        audioLengthProcess.waitFor();

        // Reading the Process output and to determine the
        // Audio length to determine the length of creation.
        BufferedReader stdout = new BufferedReader(new InputStreamReader(audioLengthProcess.getInputStream()));
        String audioLengthDouble = stdout.readLine();

        double frameRate = _numberOfImages / (Double.parseDouble(audioLengthDouble) + 1);

        String imagesDirs = Folders.CREATIONS.asString() + _searchTerm + "/*.jpg";
        String imageCommand = "cat " + imagesDirs + " | ffmpeg -f image2pipe -framerate " + frameRate + " -i - -c:v libx264 -pix_fmt yuv420p -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" -r 25 -y " + TEMP_VIDEO_DIR;
        String textCommand = "ffmpeg -y -i " + TEMP_VIDEO_DIR + " -vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _searchTerm + "'\" " + NO_SOUND_VIDEO_DIR;

        ProcessBuilder videoBuilder = new ProcessBuilder("/bin/bash", "-c", (imageCommand + ";" + textCommand));

        Process videoBuilderProcess = videoBuilder.start();
        videoBuilderProcess.waitFor();
    }

    // This method will merge the audio .wav and video .mp4 file to create
    // the creation.
    private void mergeAudioAndVideo() {
        createVideo(NO_SOUND_VIDEO_DIR, AUDIO_FILE_DIR, VIDEO_OUTPUT_DIR);
    }

    private void quizVideoCreation() {
        //the search term is the quiz video name so there is no need to repeat.
        File quizVideo = new File(QUIZ_VIDEO_DIR);
        if (!quizVideo.exists()) {
            createVideo(TEMP_VIDEO_DIR, AUDIO_FILE_DIR, QUIZ_VIDEO_DIR);
        }
    }

    private void createVideo(String videoInputDir, String audioInputDir, String videoOutputDir) {
        try {
            String command = "ffmpeg -y -i " + videoInputDir + " -i " + audioInputDir + " " + videoOutputDir;

            ProcessBuilder finalVideoBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            Process videoBuilderProcess = finalVideoBuilder.start();
            videoBuilderProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
