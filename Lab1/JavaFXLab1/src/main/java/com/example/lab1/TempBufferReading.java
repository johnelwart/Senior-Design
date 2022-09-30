package com.example.lab1;

import java.io.*;

public class TempBufferReading {
    public File file = new File("C:\\Users\\Isaac\\Senior Design\\Lab1\\Senior-Design\\AtmegatoPCBuffer.txt");

    public BufferedReader br = new BufferedReader(new FileReader(file));

    public TempBufferReading() throws FileNotFoundException {
    }

    public String getMostRecentData() throws IOException {
        String st;
        String tempString = "-127.0";
        int x = 0;
        while ((st = br.readLine()) != null) {
            tempString = st;
        }
        return tempString;
    }

}
