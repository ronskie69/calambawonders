package com.menposun.calambawonders.toursit_spots;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public interface TouristAttraction {
    int faveCounts = 0;

    void checkBundle(Bundle bundle);
    void setImageSlider(String childPath);
    void loadFavesCount(TextView textView);
    void addFavorites(String dbRef, String dbPath);
    void removeToFavorites(String dbRef, String dbPath);
    void addToPlan(String dbRef, String dbPath, String date);
    void removeToPlan(String dbRef, String dbPath);
    void addVisited(String dbRef, String dbPath);
    void removeVisited(String dbRef, String dbPath);
    void checkIfAddedToFavorites();
    void checkIfPlanned();
    void checkIfVisited();
    void updateCount(String operator);
    void redirect(Fragment fragment);
}
