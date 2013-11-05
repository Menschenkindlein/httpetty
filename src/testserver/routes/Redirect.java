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
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.List;
import testserver.Statistic;

/**
 *
 * @author maximo
 */
class Redirect extends RouteHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, HttpRequest request) {
        QueryStringDecoder qsd = new QueryStringDecoder(request.getUri());
        List<String> newUri = qsd.parameters().get("uri");
        HttpResponse response;
        if (newUri == null) {
            response = StdResponses.BADREQUEST.resp();
        }
        else {
            Statistic.addRedirect(newUri.get(0));
            response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            response.headers().set(LOCATION, newUri.get(0));
        }
        ctx.writeAndFlush(response).addListener(CLOSE);
    }
    
}
