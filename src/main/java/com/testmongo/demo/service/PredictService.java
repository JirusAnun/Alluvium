package com.testmongo.demo.service;

import com.testmongo.demo.exception.BaseException;
import com.testmongo.demo.exception.PredictException;
import com.testmongo.demo.model.Location;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.lang.Math.*;

@Service
public class PredictService {

    public String getPin(String topLeft, String btmRight, int limit, String query, double distance) throws BaseException {
        String apiKey = "8LWsjDGguehkfo16IC6ZFU-gLZnGe4jFrGwrhZbo8Xk";
        String baseUrl = "https://atlas.microsoft.com/search/fuzzy/json";
        String returnString = "";
        double[] center = calculateCenter(topLeft, btmRight);
        double[] coordinate = getCoordinatesOffset(center[0], center[1], distance);

        topLeft = coordinate[0] + "," + coordinate[1];
        btmRight = coordinate[2] + "," + coordinate[3];

        try {
            String apiUrl = baseUrl + "?api-version=1.0" +
                    "&query=" + query +
                    "&idxSet=POI" +
                    "&topLeft=" + topLeft +
                    "&btmRight=" + btmRight +
                    "&limit=" + limit +
                    "&subscription-key=" + apiKey;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                returnString = response.toString();
//                System.out.println("Azure Maps API Response:\n" + returnString);
            } else {
                returnString = "" + responseCode;
                System.err.println("Error: HTTP " + responseCode);
            }
            connection.disconnect();
            return returnString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw PredictException.predictError();
    }

    public double[] getCoordinatesOffset(double latitude, double longitude, double distanceKm) {
        double EARTH_RADIUS = 6371;
        double angularDistance = distanceKm / EARTH_RADIUS;

        double lat1 = toRadians(latitude);
        double lon1 = toRadians(longitude);

        double lat2 = asin(sin(lat1) * cos(angularDistance) + cos(lat1) * sin(angularDistance) * cos(180));
        double lon2 = lon1 + atan2(sin(180) * sin(angularDistance) * cos(lat1), cos(angularDistance) - sin(lat1) * sin(lat2));

        // Convert back to degrees
        lat2 = toDegrees(lat2);
        lon2 = toDegrees(lon2);

        // Calculate top left and bottom right coordinates
        DecimalFormat temp = new DecimalFormat("#.######");

        double topLeftLatitude = max(latitude, lat2);
        double topLeftLongitude = min(longitude, lon2);

        topLeftLatitude = Double.parseDouble(temp.format(topLeftLatitude));
        topLeftLongitude = Double.parseDouble(temp.format(topLeftLongitude));


        double bottomRightLatitude = min(latitude, lat2);
        double bottomRightLongitude = max(longitude, lon2);

        bottomRightLatitude = Double.parseDouble(temp.format(bottomRightLatitude));
        bottomRightLongitude = Double.parseDouble(temp.format(bottomRightLongitude));

        return new double[]{topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude};
    }

    public List<String> extractDataFromJson(String jsonData, int i) {

        List<String> extractedData = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode resultNode = rootNode.get("results");
            resultNode = resultNode.get(i);
            if (resultNode == null) return new ArrayList<>();
            // Extract data based on the specified parameter
            JsonNode poiNode = resultNode.get("poi");
            JsonNode poiNode2 = resultNode.get("position");
            if (poiNode != null) {
                JsonNode nameNode = poiNode.get("name");
                if (nameNode != null && nameNode.isTextual()) {
                    extractedData.add(nameNode.asText());
                }
            }

            if (poiNode2 != null) {
                JsonNode latNode = poiNode2.get("lat");
                if (latNode != null) {
                    extractedData.add(latNode.asText());
                }
                JsonNode lonNode = poiNode2.get("lon");
                if (lonNode != null) {
                    extractedData.add(lonNode.asText());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return extractedData;
    }

    public double distanceCalculator(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;
        lat1 = toRadians(lat1);
        lon1 = toRadians(lon1);
        lat2 = toRadians(lat2);
        lon2 = toRadians(lon2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        return distance;
    }

    public ArrayList<Location> setStructure(String topLeft, String btmRight, int limit, String query, int type) throws BaseException {
        double[] distance = {10, 10, 5, 5, 3, 3, 3, 3, 3, 2, 2};
        String jsonData = getPin(topLeft, btmRight, limit, query, distance[type]);
        int i = 0;
        ArrayList<Location> locations = new ArrayList<Location>();
        while (true) {
            List<String> result = extractDataFromJson(jsonData, i);
            if (result.isEmpty()) {
                break;
            }
            String lat = result.get(1);
            String lon = result.get(2);
            double[] center = calculateCenter(topLeft, btmRight);
            locations.add(new Location(result.get(0), lat, lon, distanceCalculator(center[0], center[1], Double.parseDouble(lat), Double.parseDouble(lon))));
            i++;
        }

        Collections.sort(locations);
        return locations;
    }

    public double[] calculateCenter(String topLeftCoord, String bottomRightCoord) {
        String[] topLeftParts = topLeftCoord.split(",");
        String[] bottomRightParts = bottomRightCoord.split(",");

        double topLeftLat = Double.parseDouble(topLeftParts[0]);
        double topLeftLon = Double.parseDouble(topLeftParts[1]);
        double bottomRightLat = Double.parseDouble(bottomRightParts[0]);
        double bottomRightLon = Double.parseDouble(bottomRightParts[1]);

        double centerLat = (topLeftLat + bottomRightLat) / 2.0;
        double centerLon = (topLeftLon + bottomRightLon) / 2.0;

        DecimalFormat temp = new DecimalFormat("#.######");
        centerLat = Double.parseDouble(temp.format(centerLat));
        centerLon = Double.parseDouble(temp.format(centerLon));

        return new double[]{centerLat, centerLon};
    }

    public ArrayList<Double> scoringModel(ArrayList<ArrayList<Location>> locations) {

        ArrayList<Double> respond = new ArrayList<Double>();
        double score = 0, distance = 0;
        respond.add(score);
        //hospital
        if (!locations.get(0).isEmpty()) {
            distance = locations.get(0).get(0).getDistance();

            if (distance <= 10) {
                score += (10 - distance) / 10 * 0.15;
                respond.add(1.0);
            } else {
                score -= (10 - distance) / 10 * 0.15;
                respond.add(0.0);
            }
        } else {
            score -= 0.15;
            respond.add(-1.0);
        }

        //police
        if (!locations.get(1).isEmpty()) {
            distance = locations.get(1).get(0).getDistance();

            if (distance <= 10) {
                score = score + (10 - distance) / 10 * 0.09;
                respond.add(1.0);
            } else {
                score -= (10 - distance) / 10 * 0.09;
                respond.add(0.0);
            }
        } else {
            score -= 0.09;
            respond.add(-1.0);
        }

        //school
        if (!locations.get(2).isEmpty()) {
            distance = locations.get(2).get(0).getDistance();

            if (distance <= 5) {
                score = score + (5 - distance) / 5 * 0.09;
                respond.add(1.0);
            } else {
                score -= (5 - distance) / 5 * 0.09;
                respond.add(0.0);
            }
        } else {
            score -= 0.09;
            respond.add(-1.0);
        }

        //university
        if (!locations.get(3).isEmpty()) {
            distance = locations.get(3).get(0).getDistance();

            if (distance <= 5) {
                score = score + (5 - distance) / 5 * 0.09;
                respond.add(1.0);
            } else {
                score -= (5 - distance) / 5 * 0.09;
                respond.add(0.0);
            }
        } else {
            score -= 0.09;
            respond.add(-1.0);
        }

        //bank
        if (!locations.get(4).isEmpty()) {
            distance = locations.get(4).get(0).getDistance();

            if (distance <= 3) {
                score = score + (3 - distance) / 3 * 0.09;
                respond.add(1.0);
            } else {
                score -= (3 - distance) / 3 * 0.09;
                respond.add(0.0);
            }
        } else {
            score -= 0.09;
            respond.add(-1.0);
        }

        //gas
        if (!locations.get(5).isEmpty()) {
            distance = locations.get(5).get(0).getDistance();

            if (distance <= 3) {
                score = score + (3 - distance) / 3 * 0.09;
                respond.add(1.0);
            } else {
                score -= (3 - distance) / 3 * 0.09;
                respond.add(0.0);
            }
        } else {
            score -= 0.09;
            respond.add(-1.0);
        }

        //fitness
        if (!locations.get(6).isEmpty()) {
            distance = locations.get(6).get(0).getDistance();

            if (distance <= 3) {
                score = score + (3 - distance) / 3 * 0.005;
                respond.add(1.0);
            } else {
                score -= (3 - distance) / 3 * 0.005;
                respond.add(0.0);
            }
        } else {
            score -= 0.005;
            respond.add(-1.0);
        }

        //market
        if (!locations.get(7).isEmpty()) {
            distance = locations.get(7).get(0).getDistance();

            if (distance <= 3) {
                score = score + (3 - distance) / 3 * 0.15;
                respond.add(1.0);
            } else {
                score -= (3 - distance) / 3 * 0.15;
                respond.add(0.0);
            }
        } else {
            score -= 0.15;
            respond.add(-1.0);
        }

        //pharmacy
        if (!locations.get(8).isEmpty()) {
            distance = locations.get(8).get(0).getDistance();

            if (distance <= 3) {
                score = score + (3 - distance) / 3 * 0.09;
                respond.add(1.0);
            } else {
                score -= (3 - distance) / 3 * 0.09;
                respond.add(0.0);
            }
        } else {
            score -= 0.09;
            respond.add(-1.0);
        }

        //restaurant
        if (!locations.get(9).isEmpty()) {
            distance = locations.get(9).get(0).getDistance();

            if (distance <= 2) {
                score = score + (2 - distance) / 2 * 0.15;
                respond.add(1.0);
            } else {
                score -= (2 - distance) / 2 * 0.15;
                respond.add(0.0);
            }
        } else {
            score -= 0.15;
            respond.add(-1.0);
        }

        //park
        if (!locations.get(10).isEmpty()) {
            distance = locations.get(10).get(0).getDistance();

            if (distance <= 2) {
                score = score + (2 - distance) / 2 * 0.005;
                respond.add(1.0);
            } else {
                score -= (2 - distance) / 2 * 0.005;
                respond.add(0.0);
            }
        } else {
            score -= 0.005;
            respond.add(-1.0);
        }


        System.out.println("=====================");
        System.out.println(score);
        System.out.println("=====================");

        respond.set(0, score);
        System.out.println(respond);
        return respond;
    }

}
