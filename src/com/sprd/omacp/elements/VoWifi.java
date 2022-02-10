
package com.sprd.omacp.elements;

import com.sprd.xml.parser.impl.BaseSet;
import com.sprd.xml.parser.prv.OTAKeyDefine;

public class VoWifi extends BaseSet {

    public VoWifi(int nType) {
        super(nType);
    }

    public boolean initEvn() {
        super.initEvn();

        addMainKeySet();
        addOptKeySet();
        return true;
    }

    private void addMainKeySet() {
        addMainKey(OTAKeyDefine.VOWIFI_APPLICATION_APPID);

    }

    private void addOptKeySet() {

    }
}
