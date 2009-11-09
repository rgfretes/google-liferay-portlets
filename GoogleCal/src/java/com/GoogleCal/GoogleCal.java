package com.GoogleCal;

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
 * GoogleCal Portlet Class
 */
public class GoogleCal extends GenericPortlet {

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        PortletPreferences prefs = request.getPreferences();
        String token = "";
        String secToken = "";
        boolean change = false;

        try {
            HttpServletRequest httpreq = null;
            // Use liferay api to get the parent url parameters
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
            if (!(token.equals(null)) && !(token.equals("")) && request.getParameter("gcal_tokenauth").equals("true")) {
                secToken = "";
                try {
                    secToken = AuthSubUtil.exchangeForSessionToken("https", "www.google.com", token, keytool.getPrivateKeyAsString());
                } catch (GeneralSecurityException ex) {
                    Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, ex);
                } catch (AuthenticationException ex) {
                    Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, ex);
                }
                prefs.setValue("token", token);
                prefs.setValue("secToken", secToken);
                change = true;
            }
        } catch (Exception e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
            System.out.print(e.getMessage());
        } finally {
            if (change) {prefs.store();}
        }
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletPreferences prefs = request.getPreferences();
        String secToken = prefs.getValue("secToken", "");
        PortletURL actionURL = response.createActionURL();
        actionURL.setParameter("gcal_tokenauth", "true");
        String gcalFeed = "Not Authenticated";

        if (!(secToken.equals(""))) {
            keytool keytool = new keytool();
            PrivateKey privKey = null;
            GCalendarBean gcal = new GCalendarBean();

            try {
                privKey = keytool.getPrivateKeyAsString();
            } catch (Exception ex) {
                Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, ex);
                gcalFeed = "Exception in Key Generation";
            }
            
            try {
            gcalFeed = gcal.getCalendarFeed(secToken, privKey, response.getNamespace(), ThemeUtil.ThemePath(request));
            } catch (Exception ex) {
              Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, ex);
              gcalFeed = "Exception in Feed Generation";
            }
        }

        request.setAttribute("Gcal_gcalFeed", gcalFeed);
        request.setAttribute("Gcal_actionURL", actionURL);
        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GoogleAuth_view.jsp");
        dispatcher.include(request, response);
    }

    @Override
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletURL actionURL = response.createActionURL();
        actionURL.setPortletMode(PortletMode.VIEW);
        request.setAttribute("Gcal_actionURL", actionURL);
        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GoogleAuth_edit.jsp");
        dispatcher.include(request, response);
    }

    @Override
    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GoogleAuth_help.jsp");
        dispatcher.include(request, response);
    }
}