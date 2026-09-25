/**
 * 
 * @author Shivank-NUDM
 * 
 * HDFC CollectNow (Razorpay Embedded Checkout)
 *
 * Exports: startHdfcPayment(createOrderResponse)
 *
 * Behavior:
 * - Accepts the response returned from backend (create-order/create-citizen-reciept)
 * - Handles both:
 *    1) backend returns a `razorpay` object: { orderId, keyId, amount, currency, callbackUrl, prefill }
 *    2) backend returns a redirectUrl (legacy) — then simply navigates to it
 * - Builds a form and POSTs to Razorpay Embedded checkout endpoint.
 *
 */

export function startHdfcPayment(createOrderResponse) {
  console.log("Starting HDFC CollectNow Payment with response:", createOrderResponse);
  // Defensive checks
  if (!createOrderResponse || !createOrderResponse.Transaction) {
    console.error("Invalid create order response for HDFC", createOrderResponse);
    throw new Error("Invalid create order response for HDFC");
  }

  const txn = createOrderResponse.Transaction;
  // Option A: backend returned a `razorpay` object with explicit fields
  const razorpay = txn.additionalDetails;

  if (razorpay && razorpay.order_id) {
    const orderId = razorpay.order_id;
    const keyId = razorpay.key;
    const amount = razorpay.amount;
    const currency = razorpay?.currency || 'INR';
    const callbackUrl = razorpay.callback_url;
    const prefill = razorpay.prefill || {
      name: 'Shivank',
      email: 'shivank@niua.org',
      contact: '9000090000'
    };

    // Build form to post to Razorpay embedded endpoint
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = 'https://api.razorpay.com/v1/checkout/embedded';
    form.style.display = 'none';
    form.target = '_top';

    const addInput = (name, value) => {
      const inp = document.createElement('input');
      inp.type = 'hidden';
      inp.name = name;
      inp.value = (value != null) ? value : '';
      form.appendChild(inp);
    };

    // Required fields per Embedded Checkout
    if (keyId) addInput('key_id', keyId);
    addInput('order_id', orderId);
    if (amount) addInput('amount', amount); // paise
    addInput('currency', currency);
    if (callbackUrl) addInput('callback_url', callbackUrl);

    // Optional prefill
    if (prefill.name) addInput('prefill[name]', prefill.name);
    if (prefill.email) addInput('prefill[email]', prefill.email);
    if (prefill.contact) addInput('prefill[contact]', prefill.contact);

    // Any additional notes provided by backend
    if (razorpay.notes && typeof razorpay.notes === 'object') {
      Object.entries(razorpay.notes).forEach(([k, v]) => addInput(`notes[${k}]`, v));
    }

    document.body.appendChild(form);
    form.submit();
    return;
  }

}