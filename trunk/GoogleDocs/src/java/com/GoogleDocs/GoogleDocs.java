package com.GoogleDocs;

import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;
import com.liferay.portal.util.PortalUtil;
import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;

/**
 * GoogleDocs Portlet Class
 */
public class GoogleDocs extends GenericPortlet {

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        PortletPreferences prefs = request.getPreferences();
        String token = "";
        String secToken = "";
        boolean change = false;

        try {
            HttpServletRequest httpreq = null;
            httpreq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
            secToken = prefs.getValue("secToken", "");
            keytool keytool = new keytool();
            token = httpreq.getParameter("token");
            //revoke the token and wipe the tokenpref
            if ((request.getParameter("revokeSubmit") != null)) {
                AuthSubUtil.revokeToken(secToken, keytool.getPrivateKeyAsString());
                prefs.setValue("token", "");
                prefs.setValue("secToken", "");
                change = true;
            }
            //set the token received from google
            if (!(token.equals(null)) && !(token.equals("")) && request.getParameter("gdocs_tokenauth").equals("true")) {
                secToken = "";
                try {
                    secToken = AuthSubUtil.exchangeForSessionToken("https", "www.google.com", token, keytool.getPrivateKeyAsString());
                } catch (GeneralSecurityException ex) {
                    Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, ex);
                } catch (AuthenticationException ex) {
                    Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, ex);
                }
                prefs.setValue("token", token);
                prefs.setValue("secToken", secToken);
                change = true;
            }
        } catch (Exception e) {
            Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, e);
        } finally {
            if (change) {prefs.store();}
        }
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletPreferences prefs = request.getPreferences();
        String secToken = prefs.getValue("secToken", "");
        PortletURL actionURL = response.createActionURL();
        actionURL.setParameter("gdocs_tokenauth", "true");
        String gcalFeed = "Not Authenticated";

        if (!(secToken.equals(""))) {
            keytool keytool = new keytool();
            PrivateKey privKey = null;
            GCalendarBean gcal = new GCalendarBean();

            try {
                privKey = keytool.getPrivateKeyAsString();
            } catch (Exception ex) {
                Logger.getLogger(GoogleDocs.class.getName()).log(Level.WARNING, null, ex);
                gcalFeed = "Key Generation Exception";
            }

            gcalFeed = gcal.getDocsFeed(secToken, privKey, response.getNamespace(), ThemeUtil.ThemePath(request));
        }

        request.setAttribute("Gdocs_gcalFeed", gcalFeed);
        request.setAttribute("Gdocs_actionURL", actionURL);
        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GoogleDocs_view.jsp");
        dispatcher.include(request, response);
    }

    @Override
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletURL actionURL = response.createActionURL();
        actionURL.setPortletMode(PortletMode.VIEW);
        request.setAttribute("Gdocs_actionURL", actionURL);
        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GoogleDocs_edit.jsp");
        dispatcher.include(request, response);
    }

    @Override
    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GoogleDocs_help.jsp");
        dispatcher.include(request, response);
    }
}