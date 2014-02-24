import datahandler
import arduino
import time
from threading import Timer
import traceback
from math import radians, cos, sin, asin, sqrt, degrees

"""
Coefficients used to determine how long to turn
Number of milliseconds to turn 90 deg
"""
turnCoefficientLeftDefault = 1000
turnCoefficientRightDefault = 1000

#These values will be loaded from file and automatically tuned
turnCoefficientLeft = turnCoefficientLeftDefault
turnCoefficientRight = turnCoefficientRightDefault

#Time and direction of last turn
#Used for autocal
timeLastTurn = 0
directionLastTurn = 0
angleLastTurn = 0

#Current estimated rover direction (as a bearing from North)
estimatedRoverDirection = 0

"""
How well the rover knows what direction it is facing
0: Not known at all
1: Known accurately, not moved since calculation
2: Guess, moved since calculation
"""
directionValidityStatus = 0

#GPS coordinates at last reference point
refLat = 0
refLon = 0

#Target points to navigate to
targetLat = 0
targetLon = 0

#Are we navigating?
automaticNavigation = False
lastTurnAutomatic = False
def approx_Equal(x, y, tolerance=0.001):
    return abs(x-y) <= 0.5 * tolerance * (x + y)


def distBearing(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points 
    on the earth (specified in decimal degrees)
    """
    # convert decimal degrees to radians 
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])

    # haversine formula 
    dlon = lon2 - lon1 
    dlat = lat2 - lat1 
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a)) 

    # 6367 km is the radius of the Earth
    km = 6367 * c
    
    Bearing = degrees(tan2(cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(lon2-lon1), sin(lon2-lon1)*cos(lat2))) 
    return km*1000, ((Bearing+360)%360)

def begin():
    try:
        with open('/home/root/turncal', 'r') as f:
            line = f.readline()
            splitline = ",".split(line)
            turnCoefficientLeft = float(line[0])
            turnCoefficientRight = float(line[1])
    except:
        pass #File not opened, use default values



"""
Call regularly to continue background navigation tasks
"""
def update():
    if(automaticNavigation):
        currentLat = datahandler.latestLat
        currentLon = datahandler.latestLon
        if(approx_Equal(currentLat,targetLat,0.00005) and approx_Equal(currentLon,targetLon,0.00005)):
            automaticNavigation = False
            arduino.motorCtl("S","S")
            return
        distance, bearing = distBearing(currentLon,currentLat,targetLon,targetLat)
        distanceTravelled, currentBearing = distBearing(refLon,refLat,currentLon,currentLat)
        if(distanceTravelled > 7):
            arduino.motorCtl("S","S")
			if(directionLastTurn == 0):
                delta = currentBearing - estimatedRoverDirection 
			else:
			    delta = estimatedRoverDirection - currentBearing
            estimatedRoverDirection = bearing
            if((directionValidityStatus >= 1) and lastTurnAutomatic ):
                lastTurnCoefficient = (abs(delta) / 90) / timeLastTurn
                if(directionLastTurn == 0):
                    turnCoefficientLeft = (turnCoefficientLeft * 10 + lastTurnCoefficient) / 11
                else:
                    turnCoefficientRight = (turnCoefficientRight * 10 + lastTurnCoefficient) / 11
                with open('/home/root/turncal', 'w') as f:
				    f.write(str(turnCoefficientLeft) + "," + str(turnCoefficientRight) + "\n")
            directionValidityStatus = 1
            amountToTurn = currentBearing - bearing
            if(abs(amountToTurn) > 10):
                directionValidityStatus = 2
			    lastTurnAutomatic = True
                if(amountToTurn > 0):
                    timeToTurn = (amountToTurn / 90) * turnCoefficientLeft
					angleLastTurn = amountToTurn
					directionLastTurn = 0
                    estimatedRoverDirection -= amountToTurn
                    estimatedRoverDirection = (estimatedRoverDirection + 360) % 360
                    arduino.motorCtl("B","F")
                    time.sleep(timeToTurn / 1000)
                    arduino.motorCtl("S","S")
                else:
                    timeToTurn = (abs(amountToTurn) / 90) * turnCoefficientRight
					angleLastTurn = amountToTurn
					directionLastTurn = 1
                    estimatedRoverDirection += amountToTurn
                    estimatedRoverDirection = (estimatedRoverDirection + 360) % 360
                    arduino.motorCtl("F","B")
                    time.sleep(timeToTurn / 1000)
                    arduino.motorCtl("S","S")
            refLon = currentLon
            refLat = currentLat
        arduino.motorCtl("F","F")
		
def manualTurn(direction, angle):
    automaticNavigation = False
	lastTurnAutomatic = False
	if(direction == 0):
        timeToTurn = (abs(angle) / 90) * turnCoefficientLeft
        estimatedRoverDirection -= angle
        estimatedRoverDirection = (estimatedRoverDirection + 360) % 360
        arduino.motorCtl("B","F")
        time.sleep(timeToTurn / 1000)
        arduino.motorCtl("S","S")	 
    else:
        timeToTurn = (abs(angle) / 90) * turnCoefficientLeft
        estimatedRoverDirection += amountToTurn
        estimatedRoverDirection = (estimatedRoverDirection + 360) % 360
        arduino.motorCtl("F","B")
        time.sleep(timeToTurn / 1000)
        arduino.motorCtl("S","S")

def manualStop():
    automaticNavigation = False
    arduino.motorCtl("S","S")

def manualForward():
    automaticNavigation = False
    arduino.motorCtl("F","F")

def manualBackward():
    automaticNavigation = False
    arduino.motorCtl("B","B")    	
	
def navigateAutomatically(lat,lon):
    refLat = datahandler.latestLat
    refLon = datahandler.latestLon
	targetLat = lat
	targetLon = lon
	automaticNavigation = True
	