package sd.rittal.app.printer;

import android.content.Context;
import android.os.Handler;

import sd.rittal.app.printer.bt.BtService;
import sd.rittal.app.printer.usb.UsbService;
import sd.rittal.app.printer.wifi.WifiService;

public class PrinterClassFactory {
	public static PrinterClass create(int type, Context _context, Handler _mhandler, Handler _handler){
        if(type==0){
               return new BtService(_context,_mhandler, _handler);
        }else if(type==1){
              return new WifiService(_context,_mhandler, _handler);
        }else if(type==2){
            return new UsbService(_mhandler);
      }
		return null;
  }

}
