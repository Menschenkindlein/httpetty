/*
 * Copyright 2013 maximo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package testserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import java.util.LinkedList;

/**
 *
 * @author maximo
 */
class StatisticHandler extends ChannelTrafficShapingHandler {

    private LinkedList<Object> requestInfo;
    private long readThroughput;
    
    public StatisticHandler() {
        super(500);
        this.requestInfo = new LinkedList<>();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.readThroughput = this.trafficCounter().cumulativeReadBytes() * 1000
                / (System.currentTimeMillis() - this.trafficCounter().lastCumulativeTime());
        super.channelRead(ctx, msg);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object requestInfo) {
        if (requestInfo instanceof LinkedList) {
            this.requestInfo = (LinkedList) requestInfo;
        }
    }
    
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        TrafficCounter tc = this.trafficCounter();
        requestInfo.add((Long) tc.cumulativeReadBytes());
        requestInfo.add((Long) tc.cumulativeWrittenBytes());
        requestInfo.add((Long) this.readThroughput);
        Statistic.addFullQuery(requestInfo);
        super.close(ctx, future);
    }
}
