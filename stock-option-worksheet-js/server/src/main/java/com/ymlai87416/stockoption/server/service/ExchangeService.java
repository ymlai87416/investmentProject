package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.*;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface ExchangeService {
    List<Exchange> listAllExchange();

    List<Exchange> searchExchange(Exchange exchange);

    Exchange saveExchange(Exchange exchange);

    int deleteExchange(Exchange exchange);

    int deleteAllExchange();
}
