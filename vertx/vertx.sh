java \
  -server                                           \
  -XX:+UseNUMA                                      \
  -XX:+UseParallelGC                                \
  -XX:+AggressiveOpts                               \
  -Dvertx.disableMetrics=true                       \
  -Dvertx.disableH2c=true                           \
  -Dvertx.disableWebsockets=true                    \
  -Dvertx.flashPolicyHandler=false                  \
  -Dvertx.threadChecks=false                        \
  -Dvertx.disableContextTimings=true                \
  -Dvertx.disableTCCL=true                          \
  -jar                                              \
  load-test-vertx-1.0-SNAPSHOT.jar    \
  --instances                                       \
  `grep --count ^processor /proc/cpuinfo`
