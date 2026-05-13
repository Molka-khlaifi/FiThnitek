package services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.EnumMap;
import java.util.Map;

/**
 * Handles Stripe Checkout Session creation and QR code generation.
 *
 * Replace STRIPE_SECRET_KEY with your actual Stripe test/live secret key.
 * Replace SUCCESS_URL / CANCEL_URL with your real redirect URLs.
 */
public class StripeService {

    // ── Configuration ────────────────────────────────────────────────────────
    private static final String STRIPE_SECRET_KEY =
            "";   // ← put your key here
    private static final String SUCCESS_URL = "https://yourdomain.com/success";
    private static final String CANCEL_URL  = "https://yourdomain.com/cancel";
    private static final String CURRENCY    = "eur";   // Tunisian Dinar (change if needed)

    static {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Creates a Stripe Checkout Session for {@code amountDT} (in DT)
     * and returns the hosted payment URL.
     *
     * @param amountDT  Amount in Tunisian Dinar (e.g. 15.0)
     * @param tripId    Used as the product description shown to the customer
     * @return          Stripe Checkout Session URL
     * @throws StripeException if the API call fails
     */
    public String createCheckoutSession(double amountDT, int tripId) throws StripeException {
        // Stripe requires amounts in smallest currency unit (millimes for TND → ×1000)
        long amountMillimes = Math.round(amountDT * 1000);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(amountMillimes)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Fi Thnitek – Trip #" + tripId)
                                                                .setDescription("Ride payment via Stripe")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * Generates a JavaFX {@link WritableImage} containing a QR code
     * that encodes {@code url}.
     *
     * @param url    The URL to encode (Stripe Checkout Session URL)
     * @param size   Width and height in pixels (e.g. 300)
     * @return       A JavaFX image ready to display in an {@link javafx.scene.image.ImageView}
     * @throws WriterException if encoding fails
     */
    public WritableImage generateQRImage(String url, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, size, size, hints);

        WritableImage image = new WritableImage(size, size);
        PixelWriter pw = image.getPixelWriter();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pw.setColor(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return image;
    }
}
