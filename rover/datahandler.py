import time
import arduino
import gps
import traceback
import subprocess
from pynmea import nmea

latestT = 0.0
latestP = 0.0
latestLat = 0.0
latestLong = 0.0
latestAlt = 0.0
GPSavailable = False
DateTimeSet = False
def putLogLine(s):
    with open('log.csv', 'a') as f:
        f.write(s + "\n")
 
def begin():
    putLogLine("Time, Temp, Pressure, Lat, Long, Alt, GPS Status")    

def scheduledUpdate():
    global latestT, latestP, latestLat, latestLong, latestAlt, GPSavailable, DateTimeSet
    latestT, latestP = arduino.readTP()
    sentence = gps.getSentence("$GPGGA",3)
    if(sentence == ""): #getSentence returns blank in case of error
        GPSavailable = False
    else:
        gpgga = nmea.GPGGA()
        gpgga.parse(sentence)
        if(gpgga.gps_qual == 0):
            GPSAvailable = False
        else:
            latestLat = gpgga.latitude
            latestLong = gpgga.longitude
            latestAlt = gpgga.antenna_altitude
            if(DateTimeSet == False):
                try:
                    dateTimeInfo = gps.getSentence("$GPZDA",1.5)
                    if(dateTimeInfo != ""):
                        gpzda = nmea.GPZDA()
                        gpzda.parse(dateTimeInfo)
                        if(len(gpzda.timestamp)==9):
                            hours = gpzda.timestamp[:2]
                            minutes = gpzda.timestamp[2:4]
                            seconds = gpzda.timestamp[4:6]
                            year = str(gpzda.year)
                            month = str(gpzda.month)
                            day = str(gpzda.day)
                            timestring = year + "-" + month + "-" + day
                            timestring += " "  + hours + ":" + minutes + ":" + seconds
                            #Sync system time to GPS Time
                            subprocess.call(["date","-s","\"" + timestring + "\""],shell=True)
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
        