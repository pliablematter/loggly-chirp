loggly-chirp
============

loggly-chirp is a simple App-Engine-based email server for sending log event notification emails from Loggly <http://loggly.com/> via Alert Birds <https://alertbirds.appspot.com/>. 

The Loggly + Alert Birds + loggly-chirp process goes like this:
1. A saved search is defined in Loggly (for example all log events of level SEVERE)
2. Alert Birds runs the saved search on a regular schedule and triggers an alert if results are returned
3. Alert Bird sends an HTTP request to loggly-chirp when an alert is triggered
4. loggly-chirp sends you an email with the details of the alert

Configuration
-------------
1. Copy sample-appengine-web.xml to appengine-web.xml
2. In appengine-web.xml provide values for chirp.from-address and chirp.to-address. NOTE: The from address needs to be registered with your App Engine app under Administration > Permissions
3. Create a new App Engine application and deploy
4. Create a new Alert in Alert Birds. For the endpoint, select type URL, method POST, and provide a URL in this format http://[APP_NAME].appspot.com/chirp?subject=[SUBJECT]

APP_NAME: Your App Engine application name
SUBJECT: A name to help you identify the alert

Messages
-------------
Messages will arrive in the following format. The first line contains "Loggly Notification, then the subject provided in the URL and timestamp of the last change state. The body will contain all of the parameters sent with the alert.

Subject:
Loggly Notification : SEVERE : 1369481700

Body:
id: abc123
last_state_change: 1329421700
description:
event: resolve
subject: SEVR
threshold: gt 0 events in 5 minutes (found 0 events)

Quota
-------------
If you're on the App Engine free tier you'll be limited to 100 emails a day, so you may want to limit your use to the most critical alerts only.
