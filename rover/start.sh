[[ $(find /home/root/log* | tail -1) =~ ^(.*)([0-9]{3})$ ]]
export DIR=$(printf "${BASH_REMATCH[1]}%03d" $(( 10#${BASH_REMATCH[2]} + 1 )) )
mkdir $DIR
echo $DIR
