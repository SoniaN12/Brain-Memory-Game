public class Card {
        private String symbol;
        private boolean matched;

        public Card(String symbol) {
            this.symbol = symbol;
            this.matched = false;
        }

        public String getSymbol() {
            return symbol;
        }

        public boolean isMatched() {
            return matched;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }
    }

