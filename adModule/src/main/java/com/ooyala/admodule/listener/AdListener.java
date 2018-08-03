package com.ooyala.admodule.listener;

import com.ooyala.admodule.model.Ad;

/**
 * Created by Sam22 on 6/20/15.
 */
public interface AdListener {
     void onAdReceived(Ad ad);
     void onAdFailure();
}
