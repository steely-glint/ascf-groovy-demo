/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phonefromhere.ascf.tests
import AsteriskSCF.Configuration.SipSessionManager.V1.*;
import AsteriskSCF.Core.Discovery.V1.*;
import AsteriskSCF.System.Configuration.V1.*;

ic = Ice.Util.initialize(null);
sloc = ServiceLocatorPrxHelper.checkedCast(ic.stringToProxy("LocatorService:tcp -h lock.phonefromhere.com -p 4411"));
services = Arrays.asList(sloc.locateAll(new SipConfigurationParams()));

sipconf = services.findAll { item -> ConfigurationServicePrxHelper.checkedCast(item) != null}
sc = ConfigurationServicePrxHelper.checkedCast(sipconf[0]);
  cgrps = [
    { g = new SipGeneralGroup(); g.configurationItems = [:] ; return g} ,
    { u = new SipUDPTransportGroup(); 
      u.name = "h-udp";
      u.configurationItems = [ "host" : new  SipHostItem(1,"127.0.0.1",5081)];
      return u},
    { u = new SipUDPTransportGroup();
      u.name = "h-udp1";
      u.configurationItems = [ "host" : new  SipHostItem(2,"127.0.0.1",5080)];
      return u},
    { u = new SipUDPTransportGroup(); 
      u.name = "h-udp2";
      u.configurationItems = [ "host" : new  SipHostItem(3,"192.67.4.171",5080)];
      return u}
    ];

grps = cgrps.collect{item -> item.call()}
sc.setConfiguration((ConfigurationGroup[])grps.toArray());

System.exit(0);
