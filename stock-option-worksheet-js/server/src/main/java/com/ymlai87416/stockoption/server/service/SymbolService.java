package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.*;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface SymbolService {
    List<Symbol> listAllSymbol();

    List<Symbol> listAllSymbolByExchange(Exchange exchange);

    List<Symbol> searchSymbol(Symbol symbol, boolean initChild);

    Symbol saveSymbol(Symbol symbol);

    List<Symbol> saveSymbolInBatch(List<Symbol> symbolList);

    int deleteSymbol(Symbol symbol);

    int deleteSymbolByExchange(Exchange exchange);

    int deleteAllSymbol();
}
