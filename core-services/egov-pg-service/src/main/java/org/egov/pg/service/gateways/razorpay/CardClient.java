package org.egov.pg.service.gateways.razorpay;

public class CardClient extends ApiClient {

  CardClient(String auth) {
    super(auth);
  }

  public Card fetch(String id) throws RazorpayException {
    return get(String.format(Constants.CARD_GET, id), null);
  }
}
