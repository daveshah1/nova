while [ 1 ]
do	
   python /home/root/rover/main.py > main.log 2>&1 < /dev/null
   sleep 30
done
