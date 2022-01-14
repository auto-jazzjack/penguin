touch ../redis-6.2.6/redis.conf

SHELL_PATH=`pwd -P`

echo port 6379                        > ../redis-6.2.6/redis.conf
echo daemonize yes                    >> ../redis-6.2.6/redis.conf
echo cluster-enabled yes              >> ../redis-6.2.6/redis.conf
echo appendonly no                    >> ../redis-6.2.6/redis.conf
echo save \"\"                          >> ../redis-6.2.6/redis.conf
echo pidfile $SHELL_PATH/redis-6.2.6/nodes.conf  >> ../redis-6.2.6/redis.conf


../redis-6.2.6/redis-server ../redis-6.2.6/redis.conf