<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.google.gdata.client.http.*"%>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ include file="css.jsp" %>

<portlet:defineObjects />

<%PortletPreferences prefs = renderRequest.getPreferences();%> 


<%

String actionURL = "";
String gcalFeed = "";
String err = "";
String token = "";
String secToken = "";
String requestURL ="";
String gAuthLink = "";
try {
    actionURL = request.getAttribute("Gmail_actionURL").toString();
    gcalFeed = request.getAttribute("Gmail_gcalFeed").toString();
    token = prefs.getValue("token", "");
    secToken = prefs.getValue("secToken", "");
} catch (NullPointerException e) {
    e.printStackTrace();
    err = "NullPointer in JSP";
}

if (gcalFeed.equals("Not Authenticated")) {
requestURL = AuthSubUtil.getRequestUrl("https", "www.google.com", actionURL , "https://mail.google.com/mail/feed/atom", true, true);
gAuthLink = "<a href=" + requestURL + ">Authenticate With Google</a><br/><br/>";
}
%>

<script type="text/javascript">
	jQuery(
		function() {
			var minusImage = '01_minus.png';
			var plusImage = '01_plus.png';
			jQuery(".<portlet:namespace />entry-expander").click(
				function() {
					if (this.src.indexOf('minus.png') > -1) {
						jQuery(".feed-entry-content", this.parentNode).slideUp();

						this.src = this.src.replace(minusImage, plusImage);// themeDisplay.getPathThemeImages() + "/arrows/01_plus.png";
					}
					else {
						jQuery(".feed-entry-content", this.parentNode).slideDown();

						this.src = this.src = this.src.replace(plusImage, minusImage); //themeDisplay.getPathThemeImages() + "/arrows/01_minus.png";
					}
				}
			);
		}
	);
</script>

<%=err%>
<div class="feed">
<%=gAuthLink%>
<%=gcalFeed%>
</div>
<span><a href='https://mail.google.com/mail/' target="_blank">Go to Gmail</a></span>
