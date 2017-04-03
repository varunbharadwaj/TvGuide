package com.example.vb.tvguide.data;

import android.provider.BaseColumns;

/**
 * Created by vb on 2/25/2017.
 */

public class ShowContract {

    public static final String CONTENT_AUTHORITY = "com.example.vb.tvguide.provider";

    public static class ShowEntry implements BaseColumns {
        public static final String TABLE_NAME = "shows";
        public static final String ID = "show_id";
        public static final String SEASON = "season";
        public static final String EPISODE = "episode";
        public static final String NAME = "name";
        public static final String LANGUAGE = "language";
        public static final String STATUS = "status";
        public static final String RUNTIME = "runtime";
        public static final String TYPE = "type";
        public static final String SUMMARY = "summary";
        public static final String CHANNEL = "channel";
        public static final String COUNTRY = "country";
        public static final String URL = "url";
        public static final String IMAGE = "image";
        public static final String DAYS = "days";
        public static final String TIME = "time";

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

    }
}
