package org.dimitrescu.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import org.dimitrescu.audio.SongRequest;

import java.util.ArrayList;

public class AIRadioService {
    private String prompt = "You are a music recommendation engine. Given a list of songs in the format \"Artist%SongTitle\" separated by the character ^, generate a list of 5 songs that are similar in style and mood, and would logically come next in a queue." +
            "They should be similar to all of the current songs in the queue, as if the user was choosing them. It should be mostly songs of the same bands on the queue already, BUT STRICTLY the same music styles. Don't give country for rock queues, for example. \n" +
            "\n" +
            "Your response must be a single line with exactly 5 song titles, each formatted as \"Artist%SongTitle\", separated strictly by ^ (no spaces). \n" +
            "\n" +
            "Do not include any extra text or explanations. The songs you give should be similar to ALL of the songs on the playlist, not just some. \n" +
            "\n" +
            "Here is the current queue: \n" +
            "\n";
    public AIRadioService() {
    }

    public String[] getAiSongRecommendations(ArrayList<SongRequest> queue, SongRequest currentSong) {
        Client client = Client.builder()
                .apiKey(System.getenv("GEMINI_AI_KEY")).build();

        String queueString = "";
        queueString += currentSong.toString();

        for(SongRequest r : queue) queueString += "\n" + r.toString();

        GenerateContentConfig config = GenerateContentConfig.builder().build();
        String response = client.models.generateContent("gemini-2.0-flash-001",prompt+queueString,config).text();
        try {
            return response.split("\\^");
        } catch (Exception e) {
            return client.models.generateContent("gemini-2.0-flash-001",prompt+queueString,config).text().split("\\^");
        }
    }
}
