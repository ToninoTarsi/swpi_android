package com.swpi.sintwindpi;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class StationsXMLParser {
    private static final String ns = null;

    // We don't use namespaces

    public ArrayList<Station> parsexml(String  str) throws XmlPullParserException, IOException {
    	InputStream in = new ByteArrayInputStream(str.getBytes());
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    
    
    public ArrayList<Station> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Station> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
    	ArrayList<Station> entries = new ArrayList<Station>();

        parser.require(XmlPullParser.START_TAG, ns, "swpi");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the Station tag
            if (name.equals("station")) {
                entries.add(readStation(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }


    // Parses the contents of an Station. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quStationethods for processing. Otherwise, skips the tag.
    private Station readStation(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "station");
        int ID = 0;
        String NAME =  "";
        float LAT = 0;
        float LON =  0;
        String URL = "";
        String WEBCAM = "";
        String NOTES = "";
        String TEL = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            
            if (name.equals("ID")) {
                ID = Integer.parseInt(readText(parser));
            } else if (name.equals("NAME")) {
                NAME  = readText(parser);
            } else if (name.equals("LAT")) {
                LAT  = Float.parseFloat(readText(parser));
            } else if (name.equals("LON")) {
                LON  = Float.parseFloat(readText(parser));
            } else if (name.equals("URL")) {
                URL  = readText(parser);
            } else if (name.equals("WEBCAM")) {
            	WEBCAM  = readText(parser);     
            } else if (name.equals("NOTES")) {
                NOTES = readText(parser);
            } else if (name.equals("TEL")) {
                TEL = readText(parser);
            } else {
                skip(parser);
            }
        }
        return new Station(ID, NAME, LAT, LON, URL, WEBCAM, NOTES,TEL);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
