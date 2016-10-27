package pavelsemenkov.bus.views.smsthumbnail;

import pavelsemenkov.bus.pojos.SmsMessage;
import pavelsemenkov.bus.views.ViewMvc;

/**
 * This interface corresponds to a single SMS preview element (thumbnail) which is intended to
 * be used in a list of similar elements
 */
public interface SmsThumbnailViewMvc extends ViewMvc {

    /**
     * Show thumbnail of a particular SMS message
     * @param smsMessage the message to show
     */
    void showSmsThumbnail(SmsMessage smsMessage);
}
