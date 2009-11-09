/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GoogleMail;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;
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


    public String getMailFeed(String token, PrivateKey key, String nameSpace, String themePath) {
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

            GoogleService gs = new GoogleService("UP-Liferay-5.1", "Mail", "https", "up.edu");
            gs.setAuthSubToken(token, key);
            URL feedURL = new URL("https://mail.google.com/mail/feed/atom/");

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
            while (felIter.hasNext()) {
                SyndEntryImpl entry = felIter.next();
                result += "<div class='feed-entry'><img src='" + themePath + "/arrows/01_plus.png' class='" + nameSpace + "entry-expander feed-entry-expander'>";
                result += "<span class='feed-entry-title'><a href='" + entry.getLink().replace("http:", "https:") + "' target='_blank'>" + entry.getTitle() + "</a></span>";
                SyndContentImpl content = (SyndContentImpl) entry.getDescription();
                result += "<div class='feed-entry-content' style='display:none'>" + content.getValue() + "</div>";
                result += "</div>";
            }
            result += "</div>";
            
            //result += "</BR>" + token; //debug
           

        } catch (MalformedURLException e) {
            Logger.getLogger(GoogleMail.class.getName()).log(Level.WARNING, null, e);
            result = "Malformed URL Exception";
        } catch (IOException e) {
            Logger.getLogger(GoogleMail.class.getName()).log(Level.WARNING, null, e);
            result = "IO Exception";
        } catch (ServiceException e) {
            Logger.getLogger(GoogleMail.class.getName()).log(Level.WARNING, null, e);
            result = "Service Exception";
            result += "</BR>" + e.getMessage();
        } catch (NullPointerException e) {
            Logger.getLogger(GoogleMail.class.getName()).log(Level.WARNING, null, e);
            result = "Null Pointer Exception";
        } finally {
            return result;
        } 
    }
}

