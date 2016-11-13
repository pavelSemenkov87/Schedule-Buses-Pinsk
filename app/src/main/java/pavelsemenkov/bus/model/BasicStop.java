package pavelsemenkov.bus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Павел on 06.11.2016.
 */
public class BasicStop{
    private String basicStopName;
    private int basicStopId;
    private Map<String, ArrayList<String>> NextStopSet = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicStop basicStop = (BasicStop) o;
        if (basicStopId != basicStop.basicStopId) return false;
        return basicStopName.equals(basicStop.basicStopName);
    }

    @Override
    public int hashCode() {
        int result = basicStopName.hashCode();
        result = 31 * result + basicStopId;
        return result;
    }

    public BasicStop(String basicStopName, int basicStopId, Map<String, ArrayList<String>> nextStopSet) {
        this.basicStopName = basicStopName;
        this.basicStopId = basicStopId;
        NextStopSet = nextStopSet;
    }

    public String getBasicStopName() {
        return basicStopName;
    }

    public void setBasicStopName(String basicStopName) {
        this.basicStopName = basicStopName;
    }

    public int getBasicStopId() {
        return basicStopId;
    }

    public void setBasicStopId(int basicStopId) {
        this.basicStopId = basicStopId;
    }

    public Map<String, ArrayList<String>> getNextStopSet() {
        return NextStopSet;
    }

    public void setNextStopCoord(String stop, String coord) {
        NextStopSet.get(stop).set(0, coord);
    }
    public void putNextStopSet(String stop, String coord, String id) {
        NextStopSet.put(stop, new ArrayList<String>(){});
        NextStopSet.get(stop).add(coord);
        NextStopSet.get(stop).add(id);
    }
}
