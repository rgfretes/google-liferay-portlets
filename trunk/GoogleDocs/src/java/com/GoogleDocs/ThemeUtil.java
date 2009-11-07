/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GoogleDocs;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Theme;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

/**
 *
 * @author loope
 */
public class ThemeUtil {

    public static String ThemePath(RenderRequest request) {
        String themeImagesPath = null;

        long companyId = PortalUtil.getCompanyId(request);
        
        String themeId = ParamUtil.getString(request, "themeId");

        Theme theme = ThemeLocalServiceUtil.getTheme(companyId, themeId, false);

        String themeContextPath = request.getContextPath();

        if (theme.isWARFile()) {
            themeContextPath = theme.getContextPath();
        }

        String cdnHost = PortalUtil.getCDNHost();

        themeImagesPath = cdnHost + themeContextPath + theme.getImagesPath();


        return themeImagesPath;
    }
}
