start:
	$(MAKE) cluster-start || true

stop: clean
	pkill redis-server && sleep 2

cluster-start: redis-6.2.6/src/redis-server

redis-6.2.6/src/redis-server: redis-6.2.6/redis.conf
	./redis-6.2.6/src/redis-server $< || true

clean:
	rm -rf ./dump.rdb