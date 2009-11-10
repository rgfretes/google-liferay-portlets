/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GoogleCal;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.RedirectRequiredException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;

/**
 *
 * @author loope
 * 
 * This class does the work of grabbing the calendar feeds and parsing them into html.
 * 
 */
public class GCalendarBean {

    // Tell google to revoke our secure session token
    public void invalidateToken(String secToken, PrivateKey key) {
        try {
            AuthSubUtil.revokeToken(secToken, key);
        } catch (IOException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print(ex.getMessage());
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print(ex.getMessage());
        } catch (AuthenticationException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print(ex.getMessage());
        }
    }

    public String getCalendarFeed(String token, PrivateKey key, String nameSpace, String themePath) {
        String result = "";
        CalendarFeed allCalFeed = null;
        String privfeed[] = null;
        Writer wrt = null;
        XmlWriter xmlW = null;
        //create the xml writer
        try {
            xmlW = new XmlWriter(wrt);
        } catch (IOException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
            result = "Exception in XML Writer Creation";
        }


        try {
            // Create the service and authenticate it with our token
            CalendarService gs = new CalendarService("YourApp", "https", "Yourdomain.com");
            gs.setAuthSubToken(token, key);
            URL feedURL = new URL("https", "www.google.com", "/calendar/feeds/default/owncalendars/full");
            try { // Try to get the feed
                allCalFeed = gs.getFeed(feedURL, CalendarFeed.class);
            } catch (RedirectRequiredException ex) {
                // If this exception is thrown, check to see if it's a Redirect and handle it
                    URL newfeedURL = new URL(ex.getRedirectLocation());
                    allCalFeed = gs.getFeed(newfeedURL, CalendarFeed.class);
            }
            // Read all the feeds into an array, make them basic, and secure
            privfeed = new String[allCalFeed.getEntries().size()];
            for (int i = 0; i < allCalFeed.getEntries().size(); i++) {
                CalendarEntry entry = allCalFeed.getEntries().get(i);
                ListIterator<Link> linkIter = entry.getLinks().listIterator();
                privfeed[i] = linkIter.next().getHref().replace("full", "basic").replace("http:", "https:");
            }

            for (int i = 0; i < allCalFeed.getEntries().size(); i++) {
                DateTime maxDate = new DateTime();
                maxDate.setValue(DateTime.now().getValue() + 7 * (24 * 60 * 60 * 1000)); //Days to show Events
                URL calURL = new URL(privfeed[i] + "?orderby=starttime&sortorder=ascending&max-results=10&singleevents=true&ctz=America/Los_Angeles&start-min=" + DateTime.now() + "&start-max=" + maxDate);
                HttpGDataRequest gdr = (HttpGDataRequest) gs.createFeedRequest(calURL);
                try {
                gdr.execute(); // Request the feed
                } catch (RedirectRequiredException ex) {
                    URL newfeedURL = new URL(ex.getRedirectLocation());
                    gdr = (HttpGDataRequest) gs.createFeedRequest(newfeedURL);
                    gdr.execute();
                }
                InputStream is = gdr.getResponseStream();
                InputSource ips = new InputSource(is);
                SyndFeedInput romeFeedIn = new SyndFeedInput();
                SyndFeed romeFeed = null;
                try { // Use rome to objectize the atom feed
                    romeFeed = romeFeedIn.build(ips);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
                    result = "Illegal Argument in Rome";
                } catch (FeedException ex) {
                    Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
                    result = "Exception with Feed Input to Rome";
                }
                // Write out the title
                result += "<div class='feed-title'>" + romeFeed.getTitle() + "</div>";
                List<SyndEntryImpl> feedEntryList = romeFeed.getEntries();
                ListIterator<SyndEntryImpl> felIter = feedEntryList.listIterator();
                result += "<div class='feed-entries'>";
                //read out the Calendar Entries
                while (felIter.hasNext()) {
                    SyndEntryImpl entry = felIter.next();
                    result += "<div class='feed-entry'><img src='" + themePath + "/arrows/01_plus.png' class='" + nameSpace + "entry-expander feed-entry-expander'>";
                    result += "<span class='feed-entry-title'><a href='" + entry.getLink().replace("http:", "https:") + "' target='_blank'>" + entry.getTitle() + "</a></span>";
                    SyndContentImpl content = (SyndContentImpl) entry.getContents().listIterator().next();
                    result += "<div class='feed-entry-content' style='display:none'>" + content.getValue() + "</div>";
                    result += "</div>";
                }
                result += "</div>";
            }

            result += "<br/>";

        } catch (MalformedURLException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
            result = "Malformed URL Exception";
        } catch (IOException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
            result = "IO Exception";
        } catch (ServiceException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
            result = "Service Exception <BR/>";
            result += "<BR/>" + e.getMessage() + "" + e.getResponseBody();
        } catch (NullPointerException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
            result = "Null Pointer Exception";
        } finally {

            return result;
        }
    }
}
