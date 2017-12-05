sleep_time=60
./test.sh jetty -v 1 -n 1000 -c 1
sleep $sleep_time
./test.sh vertx -v 1 -n 1000 -c 1
sleep $sleep_time
./test.sh jetty -v 1 -n 1000 -c 10
sleep $sleep_time
./test.sh vertx -v 1 -n 1000 -c 10
sleep $sleep_time
./test.sh jetty -v 1 -n 1000 -c 100
sleep $sleep_time
./test.sh vertx -v 1 -n 1000 -c 100
sleep $sleep_time
./test.sh jetty -v 1 -n 10000 -c 1000
sleep $sleep_time
./test.sh vertx -v 1 -n 10000 -c 1000
sleep $sleep_time
./test.sh jetty -v 1 -n 10000 -c 5000
sleep $sleep_time
./test.sh vertx -v 1 -n 10000 -c 5000
