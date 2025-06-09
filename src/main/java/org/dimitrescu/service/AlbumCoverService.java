package org.dimitrescu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AlbumCoverService {

    public AlbumCoverService() {
    }

    public String getAlbumCoverUrl(String songName, String artistName, String link, AudioTrack track) {
        String url = "https://api.discogs.com/database/search?q=";
        String token = "&token=" + URLEncoder.encode(System.getenv("DISCOGS_API_TOKEN"),StandardCharsets.UTF_8);
        String encodedSongName = URLEncoder.encode(songName.replaceAll("\\s*\\([^)]*\\)", "").trim(), StandardCharsets.UTF_8);
        String encodedArtistName =  URLEncoder.encode(artistName,StandardCharsets.UTF_8);

        String queryURL = url + encodedArtistName + "+" + encodedSongName +  token;

        System.out.println("[+] Querying Discogs API with URL: " + queryURL);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(queryURL))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[+] Response from Discogs API: " + response.body());
        } catch (IOException e) {
            System.err.println("[-] Error while sending request to Discogs API: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.err.println("[-] Request to Discogs API was interrupted: " + e.getMessage());
            throw new RuntimeException(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            String coverUrl = root.path("results").get(0).path("cover_image").asText();
            System.out.println("[+] Cover URL: " + coverUrl);
            return coverUrl;
        } catch (NullPointerException e) {
            System.out.println("[-] No album cover found,resorting to default youtube thumbnail");
            String id = link.contains("v=") ? link.split("v=")[1].split("&")[0] : link.substring(link.lastIndexOf("/") + 1);
            System.out.println(id);
            if(link.contains("youtube")) return "https://img.youtube.com/vi/" + id + "/hqdefault.jpg";
            else return track.getInfo().artworkUrl;
        }
    }
}
