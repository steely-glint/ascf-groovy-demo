/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phonefromhere.ascf.tests;

import AsteriskSCF.Core.Discovery.V1.*;
import AsteriskSCF.Media.RTP.V1.RTPMediaServicePrx;
import AsteriskSCF.Media.RTP.V1.RTPMediaServicePrxHelper;
import AsteriskSCF.Media.RTP.V1.RTPServiceLocatorParams;
import AsteriskSCF.Media.RTP.V1.RTPSessionPrx;
import AsteriskSCF.Media.V1.Format;
import Ice.Identity;
import Ice.ObjectPrx;

/**
 *
 * @author tim
 */
public class SimpleClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int status = 0;
        Ice.Communicator ic = null;
        try {
            ic = Ice.Util.initialize(args);
            Ice.ObjectPrx base = ic.stringToProxy("LocatorService:tcp -h lock.phonefromhere.com -p 4411");
            ServiceLocatorPrx sloc = ServiceLocatorPrxHelper.checkedCast(base);
            ServiceLocatorParams para = new ServiceLocatorParams();
            para.category = "";
            ObjectPrx[] services = sloc.locateAll(para);
            for (int i=0; i< services.length; i++){
                Identity id = services[i].ice_getIdentity();
                System.out.println("identity cat ="+id.category+" name = "+id.name);
                if (id.name.equals("RTPMediaService")){
                    RTPMediaServicePrx mp = RTPMediaServicePrxHelper.checkedCast(services[i]);
                    RTPServiceLocatorParams rp = new RTPServiceLocatorParams();
                    rp.category = "recorder";
                    Format formats[] = new Format[1];
                    formats[0] = new Format();
                    formats[0].name = "ULAW";
                    rp.formats = formats;
                    // RTPSessionPrx allocated = mp.allocate(rp);
                    // System.out.println(allocated.toString());
                }

                System.out.println(services[i].toString());
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
