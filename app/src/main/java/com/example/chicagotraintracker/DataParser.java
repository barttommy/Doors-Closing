package com.example.chicagotraintracker;

import java.io.*;
import java.util.*;

// Parser written by Arturo
public class DataParser {

    public String[] Station_Name;
    public String[] Map_ID;
    public String[] Location_X;
    public String[] Location_Y;

    public DataParser(BufferedReader file1) {
        BufferedReader reader = null;
        try {

            ArrayList<String> A = new ArrayList<String>();
            reader = file1;

            String line;
            while ((line = reader.readLine()) != null) {
                A.add(line);
            }

            Station_Name = Station_Name(A);
            Map_ID = Map_ID(A);
            Location_X = X_Cord(A);
            Location_Y = Y_Cord(A);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] Y_Cord(ArrayList<String> a) {
        String[] Y_cord = new String[(a.size()-1)];
        int holder = 0;
        for(int i = 1; i< a.size(); i++) {
            Y_cord[holder] = ((a.get(i).split(","))[3]);
            holder++;
        }
        return Y_cord;
    }
    private static String[] X_Cord(ArrayList<String> a) {
        String[] X_cord = new String[(a.size()-1)];
        int holder = 0;
        for(int i = 1; i< a.size(); i++) {
            X_cord[holder] = ((a.get(i).split(","))[2]);
            holder++;
        }
        return X_cord;
    }
    private static String[] Map_ID(ArrayList<String> a) {
        String[] stop_name = new String[(a.size()-1)];
        int holder = 0;
        for(int i = 1; i< a.size(); i++) {
            stop_name[holder] = (a.get(i).split(","))[1];
            holder++;
        }
        return stop_name;
    }
    private static String[] Station_Name (ArrayList<String> A) {
        String[] stop_id = new String[(A.size()-1)];
        int holder = 0;
        for(int i = 1; i< A.size(); i++) {
            stop_id[holder] = (A.get(i).split(","))[0];
            holder++;
        }
        return stop_id;
    }
}
