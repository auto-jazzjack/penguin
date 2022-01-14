pkill redis-server && sleep 2

touch ../redis-6.2.6/redis.conf

SHELL_PATH=`cd .. && pwd -P`

echo port 6379                        > ../redis-6.2.6/redis.conf
echo daemonize yes                    >> ../redis-6.2.6/redis.conf
echo cluster-enabled yes              >> ../redis-6.2.6/redis.conf
echo appendonly no                    >> ../redis-6.2.6/redis.conf
echo save \"\"                          >> ../redis-6.2.6/redis.conf
echo "cluster-config-file $SHELL_PATH/redis-6.2.6/nodes.conf"  >> ../redis-6.2.6/redis.conf

exec $SHELL_PATH/redis-6.2.6/redis-server $SHELL_PATH/redis-6.2.6/redis.conf