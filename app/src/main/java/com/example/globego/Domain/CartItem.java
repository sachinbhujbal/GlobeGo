package com.example.globego.Domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CartItem implements Serializable{

    private String title;
    private String address;
    private String pic;
    private int price;
    private String score;
    private String duration;
    private String timeTour;
    private String dateTour;
    private String tourGuideName;
    private String tourGuidePic;
    private int bed;



    // Add a reference to ItemDomain
    private ItemDomain itemDomain;

    public CartItem(String title, String address, int price, String score, String pic,
                    String duration, String timeTour, String dateTour,
                    String tourGuideName, String tourGuidePic, int bed) {
        this.title = title;
        this.address = address;
        this.price = price;
        this.score = score;
        this.pic = pic;
        this.duration = duration;
        this.timeTour = timeTour;
        this.dateTour = dateTour;
        this.tourGuideName = tourGuideName;
        this.tourGuidePic = tourGuidePic;
        this.bed = bed;
    }

    @NonNull
    @Override
    public String toString() {
        return pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimeTour() {
        return timeTour;
    }

    public void setTimeTour(String timeTour) {
        this.timeTour = timeTour;
    }

    public String getDateTour() {
        return dateTour;
    }

    public void setDateTour(String dateTour) {
        this.dateTour = dateTour;
    }

    public String getTourGuideName() {
        return tourGuideName;
    }

    public void setTourGuideName(String tourGuideName) {
        this.tourGuideName = tourGuideName;
    }

    public String getTourGuidePic() {
        return tourGuidePic;
    }

    public void setTourGuidePic(String tourGuidePic) {
        this.tourGuidePic = tourGuidePic;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public void setItemDomain(ItemDomain itemDomain) {
        this.itemDomain = itemDomain;
    }
    // Getter for ItemDomain
    public ItemDomain getItemDomain() {
        return itemDomain;
    }

}
