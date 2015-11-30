# Google Portlets Setup #

I suggest you use Netbeans with the [Portal Pack](http://portalpack.netbeans.org/). That's what I used to develop it.

You'll need the following libraries to build them:

## Docs ##
  * portlet-api-1.0 ([Portal Pack](http://portalpack.netbeans.org/))
  * portlettaglib ([Portal Pack](http://portalpack.netbeans.org/))
  * GData-Docs-2.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * GData-Docs-Meta-2.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-core-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-client-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-client-meta-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * Rome-1.0 ([Rome](https://rome.dev.java.net/))
  * jdom ([jdom](http://www.jdom.org/dist/binary/jdom-1.1.1.tar.gz))
  * liferay portal-kernel ([Liferay](http://liferay.com))
  * liferay portal-service ([Liferay](http://liferay.com))
  * liferay util-java ([Liferay](http://liferay.com))

## Mail ##
  * portlet-api-1.0 ([Portal Pack](http://portalpack.netbeans.org/))
  * portlettaglib ([Portal Pack](http://portalpack.netbeans.org/))
  * gdata-core-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-client-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-client-meta-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * Rome-1.0 ([Rome](https://rome.dev.java.net/))
  * jdom ([jdom](http://www.jdom.org/dist/binary/jdom-1.1.1.tar.gz))
  * liferay portal-kernel ([Liferay](http://liferay.com))
  * liferay portal-service ([Liferay](http://liferay.com))
  * liferay util-java ([Liferay](http://liferay.com))

## Calendar ##
  * portlet-api-1.0 ([Portal Pack](http://portalpack.netbeans.org/))
  * portlettaglib ([Portal Pack](http://portalpack.netbeans.org/))
  * GData-calendar-2.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * GData-calendar-Meta-2.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-core-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-client-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * gdata-client-meta-1.0 ([Gdata](http://gdata-java-client.googlecode.com/files/gdata-src.java-1.29.0.java.zip))
  * Rome-1.0 ([Rome](https://rome.dev.java.net/))
  * jdom ([jdom](http://www.jdom.org/dist/binary/jdom-1.1.1.tar.gz))
  * liferay portal-kernel ([Liferay](http://liferay.com))
  * liferay portal-service ([Liferay](http://liferay.com))
  * liferay util-java ([Liferay](http://liferay.com))


Go [register with google](http://code.google.com/apis/accounts/docs/RegistrationForWebAppsAuto.html#new) and setup your certificate then import into your keystore using keytool.

Checkout the project, edit the keytool.java to match your keytool location and password.

Alter this line in the GCalendarBean.java, putting in your app name (Portal Name) and domain that you registered.
```
GoogleService gs = new GoogleService("YourApp", "Docs", "https", "Yourdomain.com");
```
Make whatever other modifications may be necessary. (The current code is built for liferay 5.1.2)

Build and Deploy!