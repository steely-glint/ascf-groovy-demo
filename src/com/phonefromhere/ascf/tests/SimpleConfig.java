/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonefromhere.ascf.tests;

import AsteriskSCF.Configuration.SipSessionManager.V1.SipConfigurationParams;
import AsteriskSCF.Configuration.SipSessionManager.V1.SipGeneralGroup;
import AsteriskSCF.Configuration.SipSessionManager.V1.SipHostItem;
import AsteriskSCF.Configuration.SipSessionManager.V1.SipTransportGroup;
import AsteriskSCF.Configuration.SipSessionManager.V1.SipUDPTransportGroup;
import AsteriskSCF.Core.Discovery.V1.*;
import AsteriskSCF.SIP.V1.ComponentServiceDiscoveryCategory;
import AsteriskSCF.System.Configuration.V1.*;
import Ice.Identity;
import Ice.ObjectPrx;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tim
 */
public class SimpleConfig {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int status = 0;
        Ice.Communicator ic = null;
        ConfigurationGroup[] grps;

        try {
            ic = Ice.Util.initialize(args);
            Ice.ObjectPrx base = ic.stringToProxy("LocatorService:tcp -h lock.phonefromhere.com -p 4411");
            ServiceLocatorPrx sloc = ServiceLocatorPrxHelper.checkedCast(base);
            SipConfigurationParams para = new SipConfigurationParams();
            ObjectPrx[] services = sloc.locateAll(para);
            for (int i = 0; i < services.length; i++) {
                Identity id = services[i].ice_getIdentity();
                System.out.println("identity cat =" + id.category + " name = " + id.name);
                ConfigurationServicePrx configurationService = ConfigurationServicePrxHelper.checkedCast(services[i]);
                if (configurationService != null) {
                    SipGeneralGroup gen = new SipGeneralGroup();
                    gen.configurationItems = new HashMap();
                    grps = new ConfigurationGroup[3];
                    grps[0] = gen;

                    SipUDPTransportGroup stg = new SipUDPTransportGroup();
                    stg.name = "h-udp";
                    stg.configurationItems = new HashMap();
                    SipHostItem shi = new  SipHostItem();
                    shi = new SipHostItem();
                    shi.host ="127.0.0.1";
                    shi.port =5061;
                    stg.configurationItems.put("host", shi);
                    grps[1] = stg;

                    stg = new SipUDPTransportGroup();
                    stg.name = "h2-udp";
                    stg.configurationItems = new HashMap();
                    shi = new  SipHostItem();
                    shi.host ="192.67.4.171";
                    shi.port =5062;
                    stg.configurationItems.put("host", shi);
                    grps[2] = stg;

                    configurationService.setConfiguration(grps);
                    grps = configurationService.getConfigurationGroups();
                    configurationService.getConfigurationAll(grps);

                    for (int j = 0; j < grps.length; j++) {
                        Iterator<String> k;
                        ConfigurationItem val;
                        ConfigurationGroup g = grps[j];
                        System.out.println("group id =" + g.ice_id());
                        k = g.configurationItems.keySet().iterator();
                        System.out.println("k =" + g.configurationItems.size());

                        while (k.hasNext()) {
                            String key = k.next();
                            val = g.configurationItems.get(key);
                            System.out.println("\t"+key+"="+val.toString());

                        }
                    }
                }
            }

            if (sloc == null) {
                throw new Error("Invalid proxy");
            }
        } catch (Ice.LocalException e) {
            e.printStackTrace();
            status = 1;
        } catch (Exception e) {
            e.printStackTrace();
            status = 1;
        }
        if (ic != null) {
            // Clean up // 
            try {
                ic.destroy();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                status = 1;
            }
        }
    }
}
