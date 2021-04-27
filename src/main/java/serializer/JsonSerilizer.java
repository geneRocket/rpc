package serializer;

import com.alibaba.fastjson.JSONObject;

public class JsonSerilizer implements Serializer{


    @Override
    public <T> byte[] serilize(T obj) {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public <T> T deserilize(byte[] bytes,Class<T> type) {
        return JSONObject.parseObject(bytes,type);
    }
}
