/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fw.olib.com.notnoop.exceptions;

import fw.olib.com.notnoop.apns.DeliveryError;

/**
 *
 * @author kkirch
 */
public class ApnsDeliveryErrorException extends ApnsException {

	private static final long serialVersionUID = -1033332851778187072L;
	
	private final DeliveryError deliveryError;

    public ApnsDeliveryErrorException(DeliveryError error) {
        this.deliveryError = error;
    }

    @Override
    public String getMessage() {
        return "Failed to deliver notification with error code " + deliveryError.code();
    }

    public DeliveryError getDeliveryError() {
        return deliveryError;
    }
    
    
}
