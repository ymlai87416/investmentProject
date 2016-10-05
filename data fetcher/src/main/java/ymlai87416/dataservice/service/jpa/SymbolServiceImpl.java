package ymlai87416.dataservice.service.jpa;

import ymlai87416.dataservice.service.SymbolService;

/**
 * Created by Tom on 6/10/2016.
 */
public class SymbolServiceImpl implements SymbolService {
    List<Symbol> listAllSymbol();

    List<Symbol> listAllSymbolByExchange(Exchange exchange);

    List<Symbol> searchSymbol(Symbol symbol);

    void saveSymbol(Symbol symbol);

    void deleteSymbol(Symbol symbol);

    void deleteSymbolByExchange(Exchange exchange);

}
