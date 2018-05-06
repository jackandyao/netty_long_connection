import com.alibaba.fastjson.JSONObject;

/**
 * Created by jack on 2018/5/6.
 */
public class FastjsonTest {
    public static void main(String[] args) {
        String str ="{\"content\":{\"success\":\"ok\",\"name\":\"jiahp98\",\"id\":98,\"age\":0},\"id\":99,\"status\":0}";

        JSONObject json = JSONObject.parseObject(str);
        JSONObject js = json.getJSONObject("content");
        String success = js.getString("success");
        int requestId = js.getInteger("id");
        JSONObject newjson = new JSONObject();
        newjson.put("success",success);
        newjson.put("requestId",requestId);
        newjson.put("msg","保存用户信息成功");
        System.out.println(js);
    }
}
