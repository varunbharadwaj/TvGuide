package com.example.vb.tvguide;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vb on 2/21/2017.
 */

public class TVShow implements Parcelable {

    public static final Parcelable.Creator<TVShow> CREATOR = new Parcelable.Creator<TVShow>() {
        public TVShow createFromParcel(Parcel in) {
            return new TVShow(in);
        }

        public TVShow[] newArray(int size) {
            return new TVShow[size];
        }
    };
    private int id;
    private String season;
    private String episode;
    private String name;
    private String language;
    private String status;
    private String runTime;
    private String type;
    private String summary;
    private String channel;
    private String country;
    private String url;
    private String image;
    private String days;
    private String time;

    public TVShow() {
    }

    private TVShow(Parcel in) {
        String[] data = new String[14];
        in.readStringArray(data);

        id = in.readInt();
        season = data[0];
        episode = data[1];
        name = data[2];
        language = data[3];
        status = data[4];
        runTime = data[5];
        type = data[6];
        summary = data[7];
        channel = data[8];
        country = data[9];
        url = data[10];
        image = data[11];
        days = data[12];
        time = data[13];
    }

    public int getId() {
        return id;
    }

    public TVShow setId(int id) {
        this.id = id;
        return this;
    }

    public String getSeason() {
        return (!season.equals("null")) ? season : "-";
    }

    public String getEpisode() {
        return (!episode.equals("null")) ? episode : "-";
    }

    public String getName() {
        return (!name.equals("null")) ? name : "-";
    }

    public String getLanguage() {
        return (!language.equals("null")) ? language : "-";
    }

    public String getStatus() {
        return (!status.equals("null")) ? status : "-";
    }

    public String getRunTime() {
        return (!runTime.equals("null")) ? runTime : "-";
    }

    public String getType() {
        return (!type.equals("null")) ? type : "-";
    }

    public String getSummary() {
        return (!summary.equals("null")) ? summary : "-";
    }

    public TVShow setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getChannel() {
        return (!channel.equals("null")) ? channel : "-";
    }

    public String getCountry() {
        return (!country.equals("null")) ? country : "-";
    }

    public String getUrl() {
        return (!url.equals("null")) ? url : "-";
    }

    public TVShow setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getImage() {
        return (!image.equals("null")) ? image : "-";
    }

    public TVShow setImage(String image) {
        this.image = image;
        return this;
    }

    public String getDays() {
        return (!days.equals("null")) ? days : "-";
    }

    public String getTime() {
        return (!time.equals("null")) ? time : "-";
    }

    public TVShow setShowDetails(String season, String episode, String name, String language, String status, String runTime, String type) {
        this.season = season;
        this.episode = episode;
        this.name = name;
        this.language = language;
        this.status = status;
        this.runTime = runTime;
        this.type = type;
        return this;
    }

    public TVShow setSchedule(String time, String days) {
        this.time = time;
        this.days = days;
        return this;
    }

    public TVShow setNetworkDetails(String channel, String country) {
        this.channel = channel;
        this.country = country;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{season, episode, name, language, status, runTime, type, summary, channel, country, url, image, days, time});
        parcel.writeInt(id);
    }
}
