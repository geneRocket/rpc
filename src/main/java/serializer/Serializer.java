package serializer;

public interface Serializer {
    <T> byte[] serilize(T obj);
    <T> T deserilize(byte[] bytes,Class<T> type);
}
