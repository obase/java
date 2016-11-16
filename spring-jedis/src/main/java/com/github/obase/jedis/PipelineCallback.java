package com.github.obase.jedis;

import redis.clients.jedis.Pipeline;

public interface PipelineCallback {

	void doInPipeline(Pipeline pipeline);
}
