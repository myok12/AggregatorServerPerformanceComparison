sleep_time=120

work ()
{
	echo ./one.sh $@
	./one.sh $@
	sleep $sleep_time
}

echo Testing without padding
work jetty Network 0 -v 1 -t 300 -n 1000000000 -c 1
work vertx Network 0 -v 1 -t 300 -n 1000000000 -c 1
work jetty Network 0 -v 1 -t 300 -n 1000000000 -c 10
work vertx Network 0 -v 1 -t 300 -n 1000000000 -c 10
work jetty Network 0 -v 1 -t 300 -n 1000000000 -c 100
work vertx Network 0 -v 1 -t 300 -n 1000000000 -c 100
work jetty Network 0 -v 1 -t 300 -n 1000000000 -c 1000
work vertx Network 0 -v 1 -t 300 -n 1000000000 -c 1000
echo Done testing without padding

sleep $sleep_time

pad=50000
echo Testing with $pad padding
work jetty Network $pad -v 1 -t 300 -n 1000000000 -c 1
work vertx Network $pad -v 1 -t 300 -n 1000000000 -c 1
work jetty Network $pad -v 1 -t 300 -n 1000000000 -c 10
work vertx Network $pad -v 1 -t 300 -n 1000000000 -c 10
work jetty Network $pad -v 1 -t 300 -n 1000000000 -c 100
work vertx Network $pad -v 1 -t 300 -n 1000000000 -c 100
work jetty Network $pad -v 1 -t 300 -n 1000000000 -c 1000
work vertx Network $pad -v 1 -t 300 -n 1000000000 -c 1000
echo Done testing with $pad padding

#sleep $sleep_time

#work jetty Eventbus $pad -v 1 -t 300 -n 1000000000 -c 1
#work vertx Eventbus $pad -v 1 -t 300 -n 1000000000 -c 1
#work jetty Eventbus $pad -v 1 -t 300 -n 1000000000 -c 10
#work vertx Eventbus $pad -v 1 -t 300 -n 1000000000 -c 10
#work jetty Eventbus $pad -v 1 -t 300 -n 1000000000 -c 100
#work vertx Eventbus $pad -v 1 -t 300 -n 1000000000 -c 100
#work jetty Eventbus $pad -v 1 -t 300 -n 1000000000 -c 1000
#work vertx Eventbus $pad -v 1 -t 300 -n 1000000000 -c 1000

# Failing due to timeout, so disabled.
# work jetty -v 1 -t 300 -n 1000000000 -c 5000
# work vertx -v 1 -t 300 -n 1000000000 -c 5000
