# edX Tether

## Goal

To do a technical proof of concept of a tethered learning experience.
Keep students connected to the learning experience through simple, mostly low-effort
reminders as push notifications to a mobile phone.

Ideally, you would do a better job of the content here, but this is a prototype of the
technical platform.

Caveat: this is oh-so-hacky

## Implementation

  * LMS changes in https://github.com/edx/edx-platform/tree/feature/cdodge/hackathon3
    * continued developing adhoc courseware API by extending it to handle push notification tokens, tokenized authentication
    * capa change to link a problem to two (correct/incorrect) tethered units (urls) that would then be accessed by a mobile client
    * first pass at a chrome-less mobile view – still super heavy and not really mobile, just chrome-less
  * Mobile implementation in Android
    * uses google cloud messaging http://developer.android.com/google/gcm/index.html via Urban Airship
    * provides API-based login, stashing of authentication token, and push notification receiver to show the tethered web-specific view of the tethered response