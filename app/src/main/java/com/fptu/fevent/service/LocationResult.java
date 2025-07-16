package com.fptu.fevent.service;

public class LocationResult {
    public String display_name;
    public String lat;
    public String lon;

    @Override
    public String toString() {
        return display_name; // Sẽ hiển thị lên AutoCompleteTextView
    }
}
