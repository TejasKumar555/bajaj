import org.json.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        // Step 1: Parse command-line arguments
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <RollNumber> <JSONFilePath>");
            return;
        }

        String rollNumber = args[0].toLowerCase().trim();
        String jsonFilePath = args[1];

        try {
            // Step 2: Read and parse the JSON file
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Step 3: Find the first "destination" key
            String destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("Error: Key 'destination' not found in the JSON file.");
                return;
            }

            System.out.println("Debug: Destination Value = " + destinationValue);

            // Step 4: Generate a random alphanumeric string
            String randomString = generateRandomString(8);
            System.out.println("Debug: Random String = " + randomString);

            // Step 5: Concatenate values and generate MD5 hash
            String concatenatedString = rollNumber + destinationValue + randomString;
            System.out.println("Debug: Concatenated String = " + concatenatedString);

            String md5Hash = generateMD5Hash(concatenatedString);

            // Step 6: Output the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) return result;
            } else if (value instanceof JSONArray) {
                for (Object item : (JSONArray) value) {
                    if (item instanceof JSONObject) {
                        String result = findDestination((JSONObject) item);
                        if (result != null) return result;
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
