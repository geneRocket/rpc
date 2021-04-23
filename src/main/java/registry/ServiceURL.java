package registry;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ServiceURL {
     String address;
     Map<String, List<String>> params = new HashMap<>();

     public static ServiceURL parse(String data) {
          ServiceURL serviceURL = new ServiceURL();
          String[] urlSlices = data.split("\\?");
          serviceURL.address = urlSlices[0];
          //解析URL参数
          if (urlSlices.length > 1) {
               String params = urlSlices[1];
               String[] urlParams = params.split("&");
               for (String param : urlParams) {
                    String[] kv = param.split("=");
                    String key = kv[0];
                    String keyEnum = key.toUpperCase();
                    if (!keyEnum.equals("")) {
                         String[] values = kv[1].split(",");
                         serviceURL.params.put(keyEnum, Arrays.asList(values));
                    }
               }
          }
          return serviceURL;
     }
}
