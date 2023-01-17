package ds;

// Name: Leo Lin
// AndrewID: hungfanl

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class StateInformationModel {
    static final String[][] fips = {{"Alabama", "01"}, {"Alaska", "02"}, {"Arizona", "04"}, {"Arkansas", "05"}, {"California", "06"},
            {"Colorado", "08"}, {"Connecticut", "09"}, {"Delaware", "10"}, {"Florida", "12"}, {"Georgia", "13"}, {"Hawaii", "15"},
            {"Idaho", "16"}, {"Illinois", "17"}, {"Indiana", "18"}, {"Iowa", "19"}, {"Kansas", "20"}, {"Kentucky", "21"},
            {"Louisiana", "22"}, {"Maine", "23"}, {"Maryland", "24"}, {"Massachusetts", "25"}, {"Michigan", "26"},
            {"Minnesota", "27"}, {"Mississippi", "28"}, {"Missouri", "29"}, {"Montana", "30"}, {"Nebraska", "31"}, {"Nevada", "32"},
            {"New Hampshire", "33"}, {"New Jersey", "34"}, {"New Mexico", "35"}, {"New York", "36"}, {"North Carolina", "37"},
            {"North Dakota", "38"}, {"Ohio", "39"}, {"Oklahoma", "40"}, {"Oregon", "41"}, {"Pennsylvania", "42"},
            {"Rhode Island", "44"}, {"South Carolina", "45"}, {"South Dakota", "46"}, {"Tennessee", "47"}, {"Texas", "48"},
            {"Utah", "49"}, {"Vermont", "50"}, {"Virginia", "51"}, {"Washington", "53"}, {"West Virginia", "54"},
            {"Wisconsin", "55"}, {"Wyoming", "56"}};
    String nickNameResponse = fetch("https://www.britannica.com/topic/List-of-nicknames-of-U-S-States-2130544");
    String capitalResponse = fetch("https://gisgeography.com/united-states-map-with-capitals/");
    String songResponse = fetch("https://www.50states.com/songs/");
    String flowerResponse = fetch("https://statesymbolsusa.org/categories/flower");
    String flagResponse = fetch("https://www.states101.com/flags");
    HashMap<String, String> fips_code;

    StateInformationModel() {
        fips_code = new HashMap<String, String>();
        for (int i = 0; i < fips.length; i++) {
            fips_code.put(fips[i][0], fips[i][1]);
        }
    }


    /**
     * Arguments.
     *
     * @param searchTag The tag of the photo to be searched for.
     */
    public String getPopulation(String searchTag){
        String stateCode = fips_code.get(searchTag);
        String uri =
                "https://api.census.gov/data/2020/dec/pl?get=NAME,P1_001N&for=state:"
                        + stateCode
                        + "&key=a5406f80f9aefd5891b625e07ca533e36106e6f5";
        String response = fetch(uri);
        String[] info = response.split(",");
        String population = info[4].replace("\"", "");
        return population;
    }

    public String getNickName(String searchTag){
        int cutLeft = nickNameResponse.indexOf(searchTag + "</a></td><td>") + searchTag.length() + 13;
        int cutRight = nickNameResponse.indexOf("</td>", cutLeft);
        String nickname = nickNameResponse.substring(cutLeft, cutRight);
        return nickname;

    }

    public String getCapital(String searchTag){
        int cutLeft = capitalResponse.indexOf(searchTag + " (") + searchTag.length() + 2;
        int cutRight = capitalResponse.indexOf(")", cutLeft);
        String capital = capitalResponse.substring(cutLeft, cutRight);
        return capital;

    }

    public String getSong(String searchTag){
        int cut1 = songResponse.indexOf(searchTag + "</dt><dd><a")+searchTag.length() + 11;
        int cutLeft = songResponse.indexOf(">", cut1) + 1;
        int cutRight = songResponse.indexOf("<", cutLeft);
        String song = songResponse.substring(cutLeft, cutRight);
        return song;
    }


    public String doFlowerSearch(String searchTag)
            throws UnsupportedEncodingException {
        /*
         * Getting the url from html page, the reason to use another string variable
         * is if there is space in the string, we need to replace it with hyphen to match the html
         */
        String hyphen = searchTag.replace(' ', '-');
        int cut1 = flowerResponse.indexOf(hyphen.toLowerCase() + "/state-flower");
        int cutLeft = flowerResponse.indexOf("src=", cut1)+5;
        int cutRight = flowerResponse.indexOf("width=", cutLeft) -2;

        String flowerURL = flowerResponse.substring(cutLeft, cutRight);
        System.out.println(flowerURL);
        return flowerURL;
    }

    public String doFlagSearch(String searchTag)
            throws UnsupportedEncodingException {
        /*
         * Getting the url from html page, the reason to use another string variable
         * is if there is space in the string, we need to replace it with hyphen to match the html
         */
        String hyphen = searchTag.replace(' ', '-');
        int cut1 = flagResponse.indexOf("flags/" + hyphen.toLowerCase());
        int cutLeft = flagResponse.indexOf("src=", cut1) + 5;
        int cutRight = flagResponse.indexOf("alt=", cutLeft) -2;
        // https://www.states101.com


        String flagURL = "https://www.states101.com" + flagResponse.substring(cutLeft, cutRight);
        System.out.println(flagURL);
        return flagURL;
    }

    /*
     * Make an HTTP request to a given URL
     * 
     * @param urlString The URL of the request
     * @return A string of the response from the HTTP GET.  This is identical
     * to what would be returned from using curl on the command line.
     */
    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which 
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
        }
        return response;
    }
}
