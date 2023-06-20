package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpMessageParser {
    public static class Request {
        private String method;
        private String url;
        private String version;
        private Map<String, String> headers;
        private String message;

        public void setMethod(String method) {
            this.method = method;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

        public String getVersion() {
            return version;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getMessage() {
            return message;
        }
    }

    public static void decodeRequestLine(BufferedReader reader, Request request) throws IOException {
        String[] line = reader.readLine().split(" ");
        assert line.length == 3;
        request.setMethod(line[0]);
        request.setUrl(line[1]);
        request.setVersion(line[2]);
    }

    public static void decodeRequestHeader(BufferedReader reader, Request request) throws IOException {
        Map<String, String> mp = new HashMap<>(16);
        String line;
        while (!"".equals(line = reader.readLine())) {
            String[] kv = line.split(" ");
            assert kv.length == 2;
            mp.put(kv[0].trim(), kv[1].trim());
        }
        request.setHeaders(mp);
    }

    public static void decodeRequestMessage(BufferedReader reader, Request request) throws IOException {
        int len = Integer.parseInt(request.getHeaders().getOrDefault("Content-Length", "0"));
        if (len == 0) {
            return;
        }
        char[] message = new char[len];
        reader.read(message);
        request.setMessage(new String(message));
    }

    //decode InputStream
    public static Request parse2request(InputStream in) throws IOException {
        Request request = new Request();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        decodeRequestLine(reader, request);
        decodeRequestHeader(reader, request);
        decodeRequestMessage(reader, request);
        return request;
    }

    public static class Response {
        private String version;
        private int code;
        private String status;
        private Map<String, String> headers;
        private String message;

        public void setVersion(String version) {
            this.version = version;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getVersion() {
            return version;
        }

        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getMessage() {
            return message;
        }
    }

    public static String buildResponse(Request request, String response) {
        Response httpResponse = new Response();
        //response line
        httpResponse.setVersion(request.getVersion());
        httpResponse.setCode(200);
        httpResponse.setStatus("OK");
        //response headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(response.getBytes().length));
        httpResponse.setHeaders(headers);
        //response text
        httpResponse.setMessage(response);

        //response String
        StringBuilder stringBuilder=new StringBuilder();
        buildResponseLine(httpResponse,stringBuilder);
        buildResponseHeaders(httpResponse,stringBuilder);
        buildResponseMessage(httpResponse,stringBuilder);
        return new String(stringBuilder);
    }

    public static void buildResponseLine(Response response,StringBuilder stringBuilder){
        stringBuilder.append(response.getVersion()).append(" ").append(response.getCode()).append(" ").append(response.getStatus()).append("\n");
    }
    public static void buildResponseHeaders(Response response,StringBuilder stringBuilder){
        for(Map.Entry<String,String>entry:response.getHeaders().entrySet()){
            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        stringBuilder.append("\n");
    }
    public static void buildResponseMessage(Response response,StringBuilder stringBuilder){
        stringBuilder.append(response.getMessage());
    }
}

