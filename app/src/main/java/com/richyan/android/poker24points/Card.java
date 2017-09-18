package com.richyan.android.poker24points;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruhua on 9/11/2016.
 */
public class Card implements Parcelable {

    private String face;
    private  String suit;

    public Card(String face, String suit){
        this.face = face;
        this.suit = suit;
    }

    protected Card(Parcel in) {
        face = in.readString();
        suit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(face);
        dest.writeString(suit);
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public int getValue(){
        String f = getFace();
        switch (f) {
            case "ace":
                return 1;
            case "two":
                return 2;
            case "three":
                return 3;
            case "four":
                return 4;
            case "five":
                return 5;
            case "six":
                return 6;
            case "seven":
                return 7;
            case "eight":
                return 8;
            case "nine":
                return 9;
            case "ten":
                return 10;
            case "jack":
                return 11;
            case "queen":
                return 12;
            default:
                return 13;
        }

    }
    public String getFace(){
        return face;
    }

    public String getSuit(){
        return suit;
    }

    public String getImgSource(){
        return String.format("%s%s", face, suit);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
