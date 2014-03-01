import time
import arduino
import gps
import traceback
import subprocess
import os
import commands
from pynmea import nmea

latestT = 0.0
latestP = 0.0
latestLat = 0.0
latestLong = 0.0
latestAlt = 0.0
GPSavailable = False
DateTimeSet = False
def putLogLine(s):
    with open(os.environ['DIR'] + '/log.csv', 'a') as f:
        f.write(s + "\n")
 
def begin():
    putLogLine("Time, Temp, Pressure, Lat, Long, Alt, GPS Status")    

def scheduledUpdate():
    global latestT, latestP, latestLat, latestLon, latestAlt, GPSavailable, DateTimeSet
    latestT, latestP = arduino.readTP()
    sentence = gps.getSentence("$GPGGA",3)
    if(sentence == ""): #getSentence returns blank in case of error
        GPSavailable = False
    else:
        gpgga = nmea.GPGGA()
        gpgga.parse(sentence)
        if(gpgga.gps_qual == "0"):
            GPSAvailable = False
        else:
            lat = gpgga.latitude
            lon = gpgga.longitude
            realLat = float(lat[:2]) + (float(lat[2:])/60) 
            realLon = float(lon[:3]) + (float(lon[3:])/60)
            if("S" in gpgga.lat_direction):
                realLat = -realLat
            if("W" in gpgga.lon_direction):
                realLon = -realLon
            latestLat = realLat
            latestLon = realLon          
            latestAlt = gpgga.antenna_altitude
            if(DateTimeSet == False):
                try:
                    dateTimeInfo = gps.getSentence("$GPRMC",1.5)
                    if(dateTimeInfo != ""):
                        gprmc = nmea.GPRMC()
                        gprmc.parse(dateTimeInfo)
                        if(len(gprmc.timestamp)>0):
                            print gprmc.timestamp
                            hours = gprmc.timestamp[:2]
                            minutes = gprmc.timestamp[2:4]
                            seconds = gprmc.timestamp[4:6]
                            year = gprmc.datestamp[4:6]
                            month = gprmc.datestamp[2:4]
                            day = gprmc.datestamp[:2]
                            timestring = year + "-" + month + "-" + day
                            timestring += " "  + hours + ":" + minutes + ":" + seconds
                            print timestring
                            #Sync system time to GPS Time
                            status, output = commands.getstatusoutput("date -s \"" + timestring + "\"")
                            print "Date:: " + output
                            DateTimeSet = True
                except:
                    print "Oops! in datetime sync code"
                    print traceback.format_exc()
    csvLine = time.strftime("%H:%M:%S") + ","
    csvLine += str(latestT) + "," + str(latestP) + ","
    csvLine += str(latestLat) + "," + str(latestLong) + "," + str(latestAlt) 
    csvLine += "," + str(GPSavailable)
    putLogLine(csvLine)
def backgroundUpdater():
    while(True):
        startTime = time.time()
        try:
            scheduledUpdate()
        except:
            print "Oops! in scheduledUpdate"
            print traceback.format_exc()
        while((time.time() - startTime) < 1):
            pass
        
