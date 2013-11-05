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
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.util.CharsetUtil;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 * @author maximo
 */
class WithDelay extends RouteHandler {

    private final String answer;
    
    public WithDelay(String answer) {
        this.answer = answer;
    }

    @Override
    public void handle(final ChannelHandlerContext ctx, HttpRequest request) {
        ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, OK,
                        Unpooled.copiedBuffer(answer, CharsetUtil.UTF_8));
        
                response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        
                ctx.writeAndFlush(response).addListener(CLOSE);
            }
        }, 10, SECONDS);
    }
}
