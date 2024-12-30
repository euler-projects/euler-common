/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eulerframework.common.util.pipeline;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jctools.queues.MpscArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class MpscPipeline<T> implements Pipeline<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MpscPipeline.class);

    private final PipelineThread<T> pipelineThread;

    public MpscPipeline(String name, int batchSize, long batchDurationMillis, Consumer<List<T>> consumer) {
        ScheduledExecutorService timer = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder()
                .setNameFormat("pipeline-timer-" + name).setDaemon(false).build());
        ExecutorService pool = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder()
                .setNameFormat("pipeline-pool-" + name).setDaemon(false).build());
        this.pipelineThread = new PipelineThread<>(name, batchSize, batchDurationMillis, consumer);
        pool.submit(this.pipelineThread);
        timer.scheduleWithFixedDelay(this.pipelineThread::wakeup, ThreadLocalRandom.current().nextInt(0, 100),
                batchDurationMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean submit(T data) {
        return this.pipelineThread.submit(data);
    }

    public static class PipelineThread<T> implements Runnable {
        private final String name;
        private final int batchSize;
        private final long forceFlushDurationMillis;

        private volatile long batchStartTime;
        private volatile Thread currentThread;

        private final MpscArrayQueue<T> queue;
        private final Consumer<List<T>> consumer;

        public PipelineThread(String name, int batchSize, long forceFlushDurationMillis, Consumer<List<T>> consumer) {
            this.name = name;
            this.batchSize = batchSize;
            this.forceFlushDurationMillis = forceFlushDurationMillis;
            this.consumer = consumer;

            this.batchStartTime = System.currentTimeMillis();
            this.queue = new MpscArrayQueue<>(1024);
        }

        @Override
        public void run() {
            this.currentThread = Thread.currentThread();
            this.currentThread.setName(this.name);

            while (!this.currentThread.isInterrupted()) {
                while (!needFlush()) {
                    LockSupport.park(this);
                }
                flush();
            }
        }

        private boolean submit(T data) {
            boolean success = this.queue.offer(data);
            if (!success) {
                this.wakeup();
                success = this.queue.offer(data);
            }
            return success;
        }

        private void wakeup() {
            if (this.needFlush()) {
                LockSupport.unpark(this.currentThread);
            }
        }

        private boolean needFlush() {
            return System.currentTimeMillis() - this.batchStartTime > this.forceFlushDurationMillis
                    || this.queue.size() >= this.batchSize;
        }

        private void flush() {
            this.batchStartTime = System.currentTimeMillis();
            List<T> tmp = new ArrayList<>(this.queue.size());
            if (this.queue.drain(tmp::add, this.batchSize) > 0) {
                try {
                    this.consumer.accept(tmp);
                } catch (Exception e) {
                    LOGGER.error("Flush error", e);
                }
            }
        }
    }
}
