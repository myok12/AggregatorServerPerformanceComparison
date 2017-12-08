sleep_time=120

work ()
{
	date +%k:%M:%S
	echo ./test.sh $@
	./test.sh $@
	sleep $sleep_time
}

work jetty -v 1 -t 300 -n 1000000000 -c 1
work vertx -v 1 -t 300 -n 1000000000 -c 1
work jetty -v 1 -t 300 -n 1000000000 -c 10
work vertx -v 1 -t 300 -n 1000000000 -c 10
work jetty -v 1 -t 300 -n 1000000000 -c 100
work vertx -v 1 -t 300 -n 1000000000 -c 100
work jetty -v 1 -t 300 -n 1000000000 -c 1000
work vertx -v 1 -t 300 -n 1000000000 -c 1000

# Failing due to timeout, so disabled.
# work jetty -v 1 -t 300 -n 1000000000 -c 5000
# work vertx -v 1 -t 300 -n 1000000000 -c 5000
