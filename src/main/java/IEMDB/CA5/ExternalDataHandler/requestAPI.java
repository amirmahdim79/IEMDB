package IEMDB.CA5.ExternalDataHandler;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import IEMDB.CA5.Models.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;



public class requestAPI {
    public static String main(String url) throws Exception {
        URL con = new URL(url);
        URLConnection cc = con.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                cc.getInputStream()));
        String inputLine;
        String data = "";
        while ((inputLine = in.readLine()) != null)
            data += inputLine;
        in.close();

        return data;

    }
}