start:
	$(MAKE) cluster-start || true

stop:
	pkill redis-server && sleep 2

cluster-start: ./stateful/redis/src/redis-server