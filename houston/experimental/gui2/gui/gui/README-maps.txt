**** READ THIS CAREFULLY TO ENABLE MAPPING ****

I have changed from Google Maps to an offline version of OpenStreetMap. To use it you must do the following:

 --- A - The easy way --- 
1. Create the folder
(Windows) C:\Users\[username]\mapcache
(Linux) ~/mapcache

2. Extract the zip file from the team dropbox under Resources/Maps into this folder

 --- B - The hard way ---
Follow if you don't have Dropbox access or want to customise the map region

1. Download jTileDownloader from here & run the Jar:
http://svn.openstreetmap.org/applications/utils/downloading/JTileDownloader/trunk/release/jTileDownloader-0-6-1.zip

2. Select 'Bounding Box (Lat/Long)'

3. Click on 'Slippy Map Chooser'

4. Select the area around Barnes (South London). Hold down the right mouse button to pan around, and drag to select.

5. Close the map selection window

6. In the output zoom levels textbox enter 14,15,16,17,18

7. Set the outputfolder to
(Windows) C:\Users\[username]\mapcache
(Linux) ~/mapcache
[create this folder if it doesn't exist]

8. Click 'Download tiles'