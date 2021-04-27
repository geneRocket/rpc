package transport;

import common.Request;
import common.Response;

import java.util.concurrent.Future;

public interface Client {
    Future<Response> submit(Request request);

}
