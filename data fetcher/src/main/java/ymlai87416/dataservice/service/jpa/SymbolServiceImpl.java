package ymlai87416.dataservice.service.jpa;

import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.service.SymbolService;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public class SymbolServiceImpl implements SymbolService {

    @Override
    public List<Symbol> listAllSymbol() {
        return null;
    }

    @Override
    public List<Symbol> listAllSymbolByExchange(Exchange exchange) {
        return null;
    }

    @Override
    public List<Symbol> searchSymbol(Symbol symbol) {
        return null;
    }

    @Override
    public void saveSymbol(Symbol symbol) {

    }

    @Override
    public void deleteSymbol(Symbol symbol) {

    }

    @Override
    public void deleteSymbolByExchange(Exchange exchange) {

    }
}
