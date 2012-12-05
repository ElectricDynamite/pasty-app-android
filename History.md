pasty-app-android history
========================

Version master
--------------
  * Added license, putting the PastyAndroidApp unter Apache 2.0 License
  * Fixed bug with Uncaught Exception when changing orientation while showing a PastyAlertDialogFragment (#17)
  * Fixed layout of TextViews when ClickableLinks are not enabled (do not linkify) (#18)

Version 0.5.0
-------------
  * Switched to mostly use Fragments (#5)
  * Fixed context menu bug in Android 4.1 and 4.2
  * Created a layout in Pasty's colors
  * Using `AsyncTask` and `Loader` for REST API calls (#14)
  * Introduced Settings option to enable or disable clickable links
  * Retired PastyActivity and used more modern PastyClipboardActivity (#15)
  * Lots of smaller stuff

Version 0.4.0
-------------
  * Switched to API Version 2
  * Enabled Sharing from App and to the App (#11)
  * Checking network availability before trying to connect (#8)
  * Changed link in "About Dialog" from https://www.pastyapp.org to http://www.pastyapp.org/ (#12)
  * Changed Intent Extras so that the version is correctly displayed when you open Settings for the first time (#13)
  * Some code cleanup

Version 0.3.0
-------------
  * Initial release.
