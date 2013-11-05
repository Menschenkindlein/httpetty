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

import static io.netty.channel.ChannelFutureListener.CLOSE;
import io.netty.channel.ChannelHandlerContext;
import static io.netty.handler.codec.http.HttpMethod.GET;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author maximo
 */
public class RoutesTable {
    private static final Map<String, RouteHandler> rt = new HashMap<>();
    static {
        rt.put("/hello", new WithDelay("Hello World!"));
        rt.put("/redirect", new Redirect());
        rt.put("/status", new CheckStatus());
    };

    public static void dispatch(ChannelHandlerContext ctx, HttpRequest request) {
        RouteHandler route;
        if (request.getMethod() != GET) {
            route = new RouteHandler() {
                @Override
                public void handle(ChannelHandlerContext ctx, HttpRequest request) {
                    HttpResponse response = StdResponses.BADREQUEST.resp();
                    ctx.writeAndFlush(response).addListener(CLOSE);
                }
            };
        }
        else {
            QueryStringDecoder query = new QueryStringDecoder(request.getUri());
            route = rt.get(query.path());
        }
        if (route == null) {
            route = new RouteHandler() {
                @Override
                public void handle(ChannelHandlerContext ctx, HttpRequest request) {
                    HttpResponse response = StdResponses.NOTFOUND.resp();
                    ctx.writeAndFlush(response).addListener(CLOSE);
                }
            };
        }
        route.handle(ctx, request);
    }

}
