package ymlai87416.dataservice.service.jpa;

import ymlai87416.dataservice.service.ExchangeService;

/**
 * Created by Tom on 6/10/2016.
 */
public class ExchangeServiceImpl implements ExchangeService {
    List<Exchange> listAllExchange();

    List<Exchange> searchExchange(Exchange exchange);

    void saveExchange(Exchange exchange);

    void deleteExchange(Exchange exchange);
}
