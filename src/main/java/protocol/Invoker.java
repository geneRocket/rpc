package protocol;

import common.Request;
import common.Response;

public interface Invoker<T> {
    Class<T> getInterface();
    String getInterfaceName();
    Response invoke(Request request);//调用
}
