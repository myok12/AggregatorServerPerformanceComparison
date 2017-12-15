sleep_time=0

work ()
{
	echo ./one.sh $@
	./one.sh $@
	sleep $sleep_time
}

for i in {1..3}
do
  # Usage:
  #   work <service> <retrieval method> <padding_b> <delay response in ms, -1 to sync> <other ab params...>
  # e.g. 
  #   work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1

  echo Testing without padding $i
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1
  work vertx Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 10
  work vertx Network 0 -1 -v 1 -t 300 -n 1000000000 -c 10
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 100
  work vertx Network 0 -1 -v 1 -t 300 -n 1000000000 -c 100
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1000
  work vertx Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1000
  echo Done testing without padding $i

  sleep $sleep_time

  pad=50000
  echo Testing with $pad padding $i
  work jetty Network $pad -1 -v 1 -t 300 -n 1000000000 -c 1
  work vertx Network $pad -1 -v 1 -t 300 -n 1000000000 -c 1
  work jetty Network $pad -1 -v 1 -t 300 -n 1000000000 -c 10
  work vertx Network $pad -1 -v 1 -t 300 -n 1000000000 -c 10
  work jetty Network $pad -1 -v 1 -t 300 -n 1000000000 -c 100
  work vertx Network $pad -1 -v 1 -t 300 -n 1000000000 -c 100
  work jetty Network $pad -1 -v 1 -t 300 -n 1000000000 -c 1000
  work vertx Network $pad -1 -v 1 -t 300 -n 1000000000 -c 1000
  echo Done testing with $pad padding $i

  sleep $sleep_time

  echo Testing with eventbus $i
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1
  work vertx EventBusNetwork 0 -1 -v 1 -t 300 -n 1000000000 -c 1
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 10
  work vertx EventBusNetwork 0 -1 -v 1 -t 300 -n 1000000000 -c 10
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 100
  work vertx EventBusNetwork 0 -1 -v 1 -t 300 -n 1000000000 -c 100
  work jetty Network 0 -1 -v 1 -t 300 -n 1000000000 -c 1000
  work vertx EventBusNetwork 0 -1 -v 1 -t 300 -n 1000000000 -c 1000
  echo Done testing with eventbus $i

  sleep $sleep_time

  delay=100
  echo Testing with delay $i
  work jetty Network 0 $delay -v 1 -t 300 -n 1000000000 -c 1
  work vertx Network 0 $delay -v 1 -t 300 -n 1000000000 -c 1
  work jetty Network 0 $delay -v 1 -t 300 -n 1000000000 -c 10
  work vertx Network 0 $delay -v 1 -t 300 -n 1000000000 -c 10
  work jetty Network 0 $delay -v 1 -t 300 -n 1000000000 -c 100
  work vertx Network 0 $delay -v 1 -t 300 -n 1000000000 -c 100
  work jetty Network 0 $delay -v 1 -t 300 -n 1000000000 -c 1000
  work vertx Network 0 $delay -v 1 -t 300 -n 1000000000 -c 1000
  echo Done testing with delay $i

  # Failing due to timeout, so disabled.
  # work jetty -v 1 -t 300 -n 1000000000 -c 5000
  # work vertx -v 1 -t 300 -n 1000000000 -c 5000

done
