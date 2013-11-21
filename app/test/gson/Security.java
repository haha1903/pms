package test.gson;

public class Security extends Node implements Filterable {
    
    private String industry;
    
    @GsonExclude
    private String symbol;

    @Override
    public String getIndustry() {
        return industry;
    }

    @Override
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    @GsonExclude
    public String getSymbol() {
        return symbol;
    }
    
    @GsonExclude
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
