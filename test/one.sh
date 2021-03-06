function pause(){
   read -p "$*"
}
# pause 'Press [Enter] key to continue...'
ab_params="${@:5}"
jetty=0
vertx=0
if [ "$1" == "jetty" ]; then
  jetty=1
fi
if [ "$1" == "vertx" ]; then
  vertx=1
fi
method=$2
padding="&padding=$3"
if [ "$4" != "-1" ]
then
  delay="&delay=$4"
fi


if [ "$vertx" -eq "0" ] && [ "$jetty" -eq "0" ]; then
  echo Did not specify server to test.
  exit 1
fi

echo =======================================================================================================================================
echo Using parameters: $ab_params
if [ $jetty -eq 1 ]; then
        echo Testing jetty:
        date
        date -u
        echo ab $ab_params  "http://api-klaatu-app2.snc1:8080/calc?method=$method&exp=%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29$padding$delay"
        ab $ab_params  "http://api-klaatu-app2.snc1:8080/calc?method=$method&exp=%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29$padding$delay"
        date
        date -u
fi
if [ $vertx -eq 1 ]; then
        echo Testing vertx:
        date
        date -u
        echo ab $ab_params  "http://api-klaatu-app2.snc1:8081/calc?method=$method&exp=%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29$padding$delay"
        ab $ab_params  "http://api-klaatu-app2.snc1:8081/calc?method=$method&exp=%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29%2B%281%2B%281%2B%281%2B%281%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%2B%281%2B%281%2B0%29%2B%281%2B0%29%2B%281%2B0%29%29%29%29%29%29$padding$delay"
        date
        date -u
fi
