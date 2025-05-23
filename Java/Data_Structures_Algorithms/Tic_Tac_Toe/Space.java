public class Space {
    private Player player;
    private String symbol;

    public Space(String symbol) {
        this.symbol = symbol;
        this.player = new Player(symbol);
    }

    public boolean setPlayer(Player player) {
        if(symbol.equals(" ")) {
            this.player = player;
            symbol = player.getSymbol();
        }

        return symbol.equals(" ");
    }

    public Space getSpace() {
        return this;
    }

    public String toString() {
        return player.getSymbol();
    }
}
