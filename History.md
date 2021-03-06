pasty-app-android history
========================

Version 0.8.1 - 2014-10-14
--------------------------
  * Fixed a bug where the app would not show the clipboard when more than 15 items are present (Thanks Chew Chee Keng)

Version 0.8.0 - 2014-02-16
--------------------------
  * Implemented Spinner icon to open context menu
  * xxhdpi resources and xxxhdpi launcher icon
  * Now not dependend on Google APIs anymore, should work on pure Android
  * Bugfixes

Version 0.7.0 - 2013-03-05
--------------------------
  * Added push notification feature (reason for new permissions)
  * Fixed a bug where the indeterminate loader was not removed correctly (#25)
  * Code cleanup

Version 0.6.1 - 2013-02-06
--------------------------
  * Now reloading clipboard without cache after deleting an item (#19)
  * Added cache control switch for `PastyLoader`
  * Started using proguard
  * Fixed typo in about view
  * Using modern ClipboardManager API on HC and above
  * Code & logging cleanup
  * layout fixes

Version 0.6.0 - 2012-12-12 
--------------------------
  * Using device cache to provide offline clipboard (#9)
  * Added license, putting the PastyAndroidApp unter Apache 2.0 License
  * Fixed bug with Uncaught Exception when changing orientation while showing a PastyAlertDialogFragment (#17)
  * Fixed layout of TextViews when ClickableLinks are not enabled (do not linkify) (#18)
  * Created new HTML based About View

Version 0.5.0 - 2012-11-28
--------------------------
  * Switched to mostly use Fragments (#5)
  * Fixed context menu bug in Android 4.1 and 4.2
  * Created a layout in Pasty's colors
  * Using `AsyncTask` and `Loader` for REST API calls (#14)
  * Introduced Settings option to enable or disable clickable links
  * Retired PastyActivity and used more modern PastyClipboardActivity (#15)
  * Lots of smaller stuff

Version 0.4.0 - 2012-08-09
--------------------------
  * Switched to API Version 2
  * Enabled Sharing from App and to the App (#11)
  * Checking network availability before trying to connect (#8)
  * Changed link in "About Dialog" from https://www.pastyapp.org to http://www.pastyapp.org/ (#12)
  * Changed Intent Extras so that the version is correctly displayed when you open Settings for the first time (#13)
  * Some code cleanup

Version 0.3.0 - 2012-05-01
--------------------------
  * Initial release.

