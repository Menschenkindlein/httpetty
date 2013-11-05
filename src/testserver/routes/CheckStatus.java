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

package testserver.routes;

import io.netty.buffer.Unpooled;
import static io.netty.channel.ChannelFutureListener.CLOSE;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.util.CharsetUtil;
import testserver.Statistic;

/**
 *
 * @author maximo
 */
class CheckStatus extends RouteHandler {

    public CheckStatus() {
    }

    @Override
    public void handle(ChannelHandlerContext ctx, HttpRequest request) {
        StringBuilder buf = new StringBuilder();
        
        buf.append("\r\nSTATISTICS\r\n==========\r\n");
        
        buf.append("\r\nTotal queries: ");
        buf.append(Statistic.getQueriesNumber());
        
        buf.append("\r\nUniq IP: ");
        buf.append(Statistic.getQueriesNumberByIp());
        
        buf.append("\r\n\r\nQueries per IP");
        buf.append("\r\n| IP       \t| count\t| Time                   \t|\r\n");
        printStatistic(Statistic.getQueriesByIp(), buf);

        buf.append("\r\nRedirects per URI");
        buf.append("\r\n| url     \t| count\t|\r\n");
        printStatistic(Statistic.getRedirects(), buf);

        buf.append("\r\n\r\nOpened connections: ");
        buf.append(Statistic.getOpened());
        
        
        buf.append("\r\n\r\n16 last connections");
        buf.append("\r\n| src_ip    \t| URI         \t| timestamp               \t| read\t| wrtn\t| speed (bytes/sec)\t|\r\n");
        printStatistic(Statistic.get16LastRequests(), buf);
        
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        
        ctx.writeAndFlush(response).addListener(CLOSE);
    }

    private void printStatistic(Iterable<Iterable> statistic, StringBuilder buf) {
        for (Iterable line : statistic) {
            buf.append("| ");
            for (Object part : line) {
                buf.append(part.toString());
                buf.append("\t| ");
            }
            buf.append("\r\n");
        }
    }
}
