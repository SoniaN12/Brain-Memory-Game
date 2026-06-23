public class Score {
        private String playerName;
        private int moves;
        private int time;
        private int totalScore;

        public Score(String playerName, int moves, int time) {
            this.playerName = playerName;
            this.moves = moves;
            this.time = time;
            this.totalScore = moves + time;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getMoves() {
            return moves;
        }

        public int getTime() {
            return time;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public String toFileString() {
            return playerName + "," + moves + "," + time + "," + totalScore;
        }
    }

