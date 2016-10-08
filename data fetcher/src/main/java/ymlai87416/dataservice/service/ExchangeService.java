package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.Exchange;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface ExchangeService {
    List<Exchange> listAllExchange();

    List<Exchange> searchExchange(Exchange exchange);

    Exchange saveExchange(Exchange exchange);

    int deleteExchange(Exchange exchange);
}
