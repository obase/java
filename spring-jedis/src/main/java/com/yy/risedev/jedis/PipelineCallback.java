package com.yy.risedev.jedis;

import redis.clients.jedis.Pipeline;

public interface PipelineCallback {

	void doInPipeline(Pipeline pipeline);
}
