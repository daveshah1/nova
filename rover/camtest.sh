while [ 1 ]
do	
   python /home/root/rover/cam.py > cam.log 2>&1 < /dev/null
   sleep 30
done
