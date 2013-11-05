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
import io.netty.handler.codec.http.HttpRequest;
import static java.lang.Integer.parseInt;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 *
 * @author maximo
 */
public class Statistic {
    
    static int opened;
    static final Map<String, List<Object>> requestsPerIp = new HashMap<>();
    static final Queue<Iterable> lastQueries = new LinkedList<>();
    static final Map<String, Integer> redirects = new HashMap<>();
    
    public static int getQueriesNumber() {
        Integer sum = 0;
        for (List<Object> value : requestsPerIp.values()) {
            sum += (Integer) value.get(0);
        }
        return sum; 
    }

    public static int getQueriesNumberByIp() {
        return requestsPerIp.size(); 
    }

    public static Iterable<Iterable> getQueriesByIp() {
        LinkedList<Iterable> answer = new LinkedList<>();
        for (Entry<String, List<Object>> ip : requestsPerIp.entrySet()) {
            LinkedList<Object> subanswer = new LinkedList<>();
            subanswer.add(ip.getKey());
            subanswer.addAll(ip.getValue());
            answer.add(subanswer);
        }
        return answer;
    }

    public static Iterable<Iterable> getRedirects() {
        LinkedList<Iterable> answer = new LinkedList<>();
        for (Entry<String, Integer> redirect : redirects.entrySet()) {
            LinkedList<Object> subanswer = new LinkedList<>();
            subanswer.add(redirect.getKey());
            subanswer.add(redirect.getValue());
            answer.add(subanswer);
        }
        return answer;
    }

    public static int getOpened() {
        return opened;
    }

    public static Iterable<Iterable> get16LastRequests() {
        return lastQueries;
    }

    public static void addQuery(ChannelHandlerContext ctx, HttpRequest request) {
        String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        List<Object> found = requestsPerIp.get(ip);
        List<Object> row= new LinkedList<>();
        if (found == null) {
            row.add(1);
        }
        else {
            row.add((Integer) found.get(0) + 1);
        }
        Calendar cal = Calendar.getInstance();
    	row.add(cal.getTime());
        requestsPerIp.put(ip, row);
        
        if (lastQueries.size() >= 16) {
            lastQueries.remove();
        }
        
        row = new LinkedList<>();
        row.add(ip);
        row.add(request.getUri());
        row.add(cal.getTime());
        ctx.pipeline().fireUserEventTriggered(row);
        
    }

    static void addFullQuery(LinkedList<Object> requestInfo) {
        lastQueries.add(requestInfo);
    }

    public static void addRedirect(String url) {
        Integer found = redirects.get(url);
        if (found == null) {
            redirects.put(url, 1);
        }
        else {
            redirects.put(url, found + 1);
        }
    }
}
