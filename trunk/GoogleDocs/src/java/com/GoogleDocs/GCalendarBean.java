/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GoogleDocs;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;

/**
 *
 * @author loope
 */
public class GCalendarBean {

    public void invalidateToken(String secToken, PrivateKey key) {
        try {
            AuthSubUtil.revokeToken(secToken, key);
        } catch (IOException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthenticationException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDocsFeed(String token, PrivateKey key, String nameSpace, String themePath) {
        String result = "";
        Writer wrt = null;
        XmlWriter xmlW = null;
        try {
            xmlW = new XmlWriter(wrt);
        } catch (IOException ex) {
            Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
            result = "XML Writer Exception";
        }

        try {

            GoogleService gs = new GoogleService("YourApp", "Docs", "https", "Yourdomain.com");
            gs.setAuthSubToken(token, key);
            URL feedURL = new URL("https://docs.google.com/feeds/documents/private/full?showfolders=false&max-results=10");

            GDataRequest gdr = gs.createFeedRequest(feedURL);
            gdr.execute();
            InputStream is = gdr.getResponseStream();
            InputSource ips = new InputSource(is);
            SyndFeedInput romeFeedIn = new SyndFeedInput();
            SyndFeed romeFeed = null;
            try {
                romeFeed = romeFeedIn.build(ips);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
                result = "Rome Argument Exception";
            } catch (FeedException ex) {
                Logger.getLogger(GCalendarBean.class.getName()).log(Level.SEVERE, null, ex);
                result = "Rome Feed Exception";
            }
            result += "<div class='feed-title'>" + romeFeed.getTitle() + "</div>";
            List<SyndEntryImpl> feedEntryList = romeFeed.getEntries();
            ListIterator<SyndEntryImpl> felIter = feedEntryList.listIterator();
            result += "<div class='feed-entries'>";
            //read out the Calendar Entries
            String folder = "";
            int hiddenNum = 0;
            //ArrayList<String> calEntries;
            while (felIter.hasNext()) {
                StringBuffer tempEntry = new StringBuffer();
                boolean viewed = false;
                boolean hidden = false;
                SyndEntryImpl entry = felIter.next();
                tempEntry.append("<div class='feed-entry'><span class='feed-entry-title'>")/*<img src='" + themePath + "/arrows/01_plus.png' class='" + nameSpace + "entry-expander feed-entry-expander'>"*/;
                List catList = entry.getCategories();
                ListIterator catIter = catList.listIterator();
                while (catIter.hasNext()) {
                    String catEntry = "";
                    SyndCategoryImpl category = (SyndCategoryImpl) catIter.next();
                    catEntry = category.getName().substring(category.getName().indexOf("#") + 1);
                    if (catEntry.equals("document") ||
                            catEntry.equals("presentation") ||
                            catEntry.equals("spreadsheet") ||
                            catEntry.equals("form") ||
                            catEntry.equals("pdf") ||
                            catEntry.equals("starred")) {
                        tempEntry.append("<img src='/GoogleDocs/" + catEntry + ".png'/> ");
                    } else if (catEntry.equals("viewed")) {
                        viewed = true;
                    } else if (catEntry.equals("hidden")) {
                        //Don't show hidden files!
                        hidden = true;
                        hiddenNum++;
                    } else if (!folder.equals(catEntry)) {
                        tempEntry.append("<img src='/GoogleDocs/folder.png'/>" + catEntry + "<br/>&nbsp&nbsp");
                        folder = catEntry;
                    } else {
                        tempEntry.insert(55, "&nbsp&nbsp");
                    }
                }
                if (!hidden) {
                    result += tempEntry.toString();
                    result += "<a href='" + entry.getLink().replace("http:", "https:") + "' target='_blank'>" + entry.getTitle() + "</a>";
                    result += (viewed) ? "" : "<img src='/GoogleDocs/new.gif'/>";
                    result += "</span>";
                    //SyndContentImpl content = (SyndContentImpl) entry.getDescription();
                    //result += "<div class='feed-entry-content' style='display:none'>" + content.getValue() + "</div>";
                    result += "</div>";
                }
            }
            result += (feedEntryList.size() - hiddenNum) + "/10 Items Displayed";
            if (0 < hiddenNum) {
                result += " (" + hiddenNum + " Items Hidden)";
            }
            result += "</div>";
            //result += "</BR>" + token; //debug


        } catch (MalformedURLException e) {
            Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, e);
            result = "Malformed URL Exception";
        } catch (IOException e) {
            Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, e);
            result = "IO Exception";
        } catch (ServiceException e) {
            Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, e);
            result = "<strong>Service Exception</strong><br/>";
            result += e.getMessage() + ": " + e.getResponseBody();
        } catch (NullPointerException e) {
            Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, e);
            result = "Null Pointer Exception";
        } finally {
            return result;
        }
    }
}

