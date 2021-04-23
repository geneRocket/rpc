package serialize;
import com.alibaba.fastjson.JSONObject;

public class JsonSerializer implements  Serializer{
    @Override
    public <T> byte[] serialize(T obj) {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        return JSONObject.parseObject(data, cls);
    }
}
