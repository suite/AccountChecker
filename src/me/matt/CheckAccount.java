package me.matt;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Matt on 2/16/17.
 */
public class CheckAccount {
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    String foundtoken = "";

    private String username;
    private String password;

    CheckAccount(String user, String pass) {
        username = user;
        password = pass;
    }

    public void validate() {
        try {
            String url = "https://account.mojang.com/login";

            URL obj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();


            CookieHandler.setDefault(new CookieManager());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            String token = new String();
            Pattern MY_PATTERN = Pattern.compile("name=\"authenticityToken\" value=\"([a-f0-9]+)\"");
            while ((inputLine = in.readLine()) != null) {

                Matcher m = MY_PATTERN.matcher(inputLine);
                if (m.find()) {
                    token = m.group(1);

                }
            }
            List<String> cookies = new ArrayList<String>();
            cookies = con.getHeaderFields().get("Set-Cookie");
            in.close();

            //print result

            con.disconnect();
            obj = new URL(url);
            con = (HttpsURLConnection) obj.openConnection();

            // s/name="authenticityToken" value="([a-f0-9]+)"/
            //    href="/me/renameProfile/([a-f0-9]+)">Change</a>
            //<input type="hidden" name="authenticityToken" value="db6cb2281c5430e015a68a3ec11e79e63c29b920">

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "authenticityToken=" + token + "&username=" + (username) + "&password=" + (password);

            // Send post request
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            con.setRequestProperty("Content-Length", Integer.toString(urlParameters.length()));

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();

            wr.close();
            con.connect();


            Pattern MY_PATTERN2 = Pattern.compile("href=\"/me/renameProfile/([a-f0-9]+)\">Change</a>");
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);

                Matcher m = MY_PATTERN2.matcher(inputLine);
                if (m.find()) {
                    token = m.group(1);

                    foundtoken = token;

                }
            }
            in.close();

            if(response.toString().contains("To confirm your identity, please answer the questions below.")) {
                System.out.println("1"); //non full access
                return;
            }
            if(foundtoken != "") {
                System.out.println("2"); //full access
            } else {
                System.out.println("0"); //invalid
            }

        } catch (IOException e) {
            System.out.println("3"); //error

        }

    }
}
