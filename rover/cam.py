import commands, os, time, shutil
while True:
	status, output = commands.getstatusoutput("mplayer tv:// -vo png:z=7 -ss 1 -frames 1 -loop 1 -tv driver=v4l2:device=/dev/video0")
	print output
        shutil.copy("00000001.png",os.environ['DIR'] + "/" + time.strftime("%H:%M:%S") + "-cap.png")
        os.rename("00000001.png","/var/www/image.png")
        time.sleep(0.1)
