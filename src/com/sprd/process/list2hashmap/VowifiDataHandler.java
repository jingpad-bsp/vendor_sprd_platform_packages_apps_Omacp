
package com.sprd.process.list2hashmap;

import java.util.HashMap;
import java.util.List;
import android.util.Log;
import com.sprd.xml.parser.prv.Define;

public class VowifiDataHandler extends BaseHandlerImpl {

    @Override
    public int process(OtaMapData data) {

        return vowifiProcess(data);
    }

    private int vowifiProcess(OtaMapData data) {
        Log.d(TAG, "vowifiProcess in");
        if (data == null) {
            return Define.STATE_PARAM_ERROR;
        }
        List<MyHashMap> appList = data.get(Define.CHAR_VODAFONEVOWIFI);
        for (HashMap<String, String> vowifiMap : appList) {
            String epdgfqdn = vowifiMap.get(Define.EPDGFQDN);
            if (null == epdgfqdn) {
                Log.d(TAG, "vowifiProcess no epdgfqdn ,so return");
                continue;
            }
                Log.d(TAG, "vowifiProcess data.put");
                data.put(Define.TYPE_VOWIFI, (MyHashMap) vowifiMap);

        }
        return Define.STATE_OK;
    }

    private final String TAG = "VowifiDataHandler";
}
