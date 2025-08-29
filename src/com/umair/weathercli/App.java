package com.umair.weathercli;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Simple Weather CLI.
 * Usage:
 *   java -cp out com.umair.weathercli.App "Toronto"
 *   java -cp out com.umair.weathercli.App "New York"
 * 
 * It uses Open-Meteo's free APIs (no API key required):
 *   - Geocoding: https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1
 *   - Forecast:  https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true
 *
 * NOTE: This is intentionally dependency-free (no JSON libraries). It uses a tiny
 * string-based extractor for the small fields we need.
 */
public class App {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -cp out com.umair.weathercli.App \"City name\"");
            System.out.println("Example: java -cp out com.umair.weathercli.App \"Toronto\"");
            return;
        }

        String query = String.join(" ", args).trim();
        try {
            // 1) Geocode the city name to lat/lon
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?count=1&name=" + urlEncode(query);
            String geoJson = HttpUtil.get(geoUrl, 12);
            if (geoJson == null || !geoJson.contains("\"results\"")) {
                System.out.println("Could not geocode location: " + query);
                return;
            }

            // Restrict to the first result object for robustness.
            String first = JsonMini.firstObjectFromArray(geoJson, "\"results\"");
            if (first == null) {
                System.out.println("No results found for: " + query);
                return;
            }

            String name = JsonMini.findString(first, "\"name\"");
            String admin1 = JsonMini.findString(first, "\"admin1\"");
            String country = JsonMini.findString(first, "\"country\"");
            Double lat = JsonMini.findNumber(first, "\"latitude\"");
            Double lon = JsonMini.findNumber(first, "\"longitude\"");

            if (lat == null || lon == null) {
                System.out.println("Couldn't parse coordinates for: " + query);
                return;
            }

            String resolved = name;
            if (admin1 != null && !admin1.isEmpty()) resolved += ", " + admin1;
            if (country != null && !country.isEmpty()) resolved += ", " + country;

            // 2) Fetch current weather for those coordinates
            String wxUrl = "https://api.open-meteo.com/v1/forecast?current_weather=true&latitude=" + lat + "&longitude=" + lon;
            String wxJson = HttpUtil.get(wxUrl, 12);
            if (wxJson == null || !wxJson.contains("\"current_weather\"")) {
                System.out.println("Couldn't fetch weather for coordinates: " + lat + "," + lon);
                return;
            }

            String cur = JsonMini.findObject(wxJson, "\"current_weather\"");
            Double temp = JsonMini.findNumber(cur, "\"temperature\"");
            Double wind = JsonMini.findNumber(cur, "\"windspeed\"");
            Double windDir = JsonMini.findNumber(cur, "\"winddirection\"");
            Integer isDay = JsonMini.findInt(cur, "\"is_day\"");
            String time = JsonMini.findString(cur, "\"time\"");

            System.out.println("Location: " + resolved + " (" + lat + ", " + lon + ")");
            System.out.println("Time:     " + (time != null ? time : "N/A"));
            System.out.println("Temp:     " + (temp != null ? String.format("%.1f °C", temp) : "N/A"));
            System.out.println("Wind:     " + (wind != null ? String.format("%.1f km/h", wind) : "N/A") +
                               (windDir != null ? " @ " + String.format("%.0f°", windDir) : ""));
            System.out.println("Daylight: " + (isDay != null ? (isDay == 1 ? "Day" : "Night") : "N/A"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String urlEncode(String s) {
        try { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }
}
