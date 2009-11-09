<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<portlet:defineObjects />

<%
String actionURL ="NoURL";
String err = "";
try {
actionURL = request.getAttribute("Gmail_actionURL").toString();
} catch (NullPointerException e) {
    err = e.getLocalizedMessage();
}
%>
<b>
    Preferences
</b>

<div>
    <FORM ACTION="<%=actionURL%>" METHOD=POST>
    <INPUT name="revokeSubmit" TYPE="submit" Value="Revoke Authentication">
    </FORM>
</div>
<%=err%>