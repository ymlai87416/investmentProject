package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface SymbolService {
    List<Symbol> listAllSymbol();

    List<Symbol> listAllSymbolByExchange(Exchange exchange);

    List<Symbol> searchSymbol(Symbol symbol);

    void saveSymbol(Symbol symbol);

    void deleteSymbol(Symbol symbol);

    void deleteSymbolByExchange(Exchange exchange);

}
