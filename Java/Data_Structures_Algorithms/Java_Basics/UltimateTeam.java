package assmt9;

import java.util.ArrayList;

public class UltimateTeam {
    private ArrayList<UltimatePlayer> players;
    private ArrayList<Coach> coaches;

    public UltimateTeam(ArrayList<UltimatePlayer> players, ArrayList<Coach> coaches) {
        this.players = players;
        this.coaches = coaches;
    }

    public String organizePlayersByPosition() {
        ArrayList<UltimatePlayer> cutters = new ArrayList<UltimatePlayer>();
        for(UltimatePlayer p:players) {
            if(p.getPosition().equals("cutter")) {
                cutters.add(p);
            }
        }

        ArrayList<UltimatePlayer> handlers = new ArrayList<UltimatePlayer>();
        for(UltimatePlayer p:players) {
            if(p.getPosition().equals("handler")) {
                handlers.add(p);
            }
        }

        String output = "CUTTERS\n";
        for(UltimatePlayer p:cutters) {
            output += p.toString() + "\n";
        }

        output += "\nHANDLERS\n";
        for(UltimatePlayer p:handlers) {
            output += p.toString() + "\n";
        }

        return output;
    }

    public UltimatePlayer getBestPlayer() {
        UltimatePlayer bestPlayer = players.get(0);
        double maxPower = bestPlayer.getPower();

        for(int i=1; i<players.size(); i++) {
            if(players.get(i).getPower() > maxPower) {
                bestPlayer = players.get(i);
                maxPower = bestPlayer.getPower();
            }
        }

        return bestPlayer;
    }

    public String toString() {
        String output = "COACHES\n";
        for(Coach c:coaches) {
            output += c + "\n";
        }

        output += "\nPLAYERS\n";
        for(UltimatePlayer p:players) {
            output += p + "\n";
        }

        return output;
    }
}
