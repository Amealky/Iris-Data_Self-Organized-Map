package bm.classification.som.irisdatasom;

import java.util.ArrayList;

public class IrisVector {
    ArrayList<Double> values = new ArrayList<>();
    String label;

    IrisVector(ArrayList<Double> values, String label) {
        this.values = values;
        this.label = label;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public Double getValue(int index) {
        return values.get(index);
    }

    public String getLabel() {
        return label;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getVectorSize() {
        return values.size();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for(Double value : values) {
            str.append(value).append(", ");
        }
        str.append("type => ").append(label);
        return str.toString();
    }
}
