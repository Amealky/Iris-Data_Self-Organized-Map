package bm.classification.som.irisdatasom;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SOM {

    public ArrayList<ArrayList<ArrayList<Double>>> weightMap = new ArrayList<>();
    IrisVector averageIrisVector;
    IrisVector stdDevVector;

    public ArrayList<IrisVector> normalizeData(ArrayList<IrisVector> irisVectors) {
        ArrayList<IrisVector> dataNormalized = new ArrayList<>();

        for (IrisVector vector : irisVectors) {
            ArrayList<Double> normalizedValues = new ArrayList<>();
            for (int col = 0; col < vector.getVectorSize(); col++) {
                normalizedValues.add((vector.getValue(col) - averageIrisVector.getValue(col)) / stdDevVector.getValue(col));
            }

            IrisVector normalizedVector = new IrisVector(normalizedValues, "Normalized Vector");
            dataNormalized.add(normalizedVector);
        }

        return dataNormalized;
    }


    public void calculateAverageVector(ArrayList<IrisVector> data) {
        int numCols = data.get(0).getVectorSize();
        ArrayList<Double> averageValues = new ArrayList<>(numCols);

        for (int col = 0; col < numCols; col++) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (IrisVector vector : data) {
                stats.addValue(vector.getValue(col));
            }
            averageValues.add(stats.getMean());
        }

        this.averageIrisVector = new IrisVector(averageValues, "Average Vector");
    }

    public void calculateStdDevVector(ArrayList<IrisVector> data) {
        int numCols = data.get(0).getVectorSize();
        ArrayList<Double> stdDevValues = new ArrayList<>(numCols);

        for (int col = 0; col < numCols; col++) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (IrisVector vector : data) {
                stats.addValue(vector.getValue(col));
            }
            stdDevValues.add(stats.getStandardDeviation());
        }

        this.stdDevVector = new IrisVector(stdDevValues, "Standard Deviation Vector");
    }

    public void initializeWeightMap(int mapHeight, int mapWidth, int vectorSize) {
        Random random = new Random();
        weightMap = new ArrayList<>();

        for (int i = 0; i < mapHeight; i++) {
            ArrayList<ArrayList<Double>> row = new ArrayList<>();
            for (int j = 0; j < mapWidth; j++) {
                ArrayList<Double> neuronWeights = new ArrayList<>();
                for (int k = 0; k < vectorSize; k++) {
                    neuronWeights.add(random.nextDouble());
                }
                row.add(neuronWeights);
            }
            weightMap.add(row);
        }
    }

    public void learn(ArrayList<ArrayList<ArrayList<Double>>> mapWeights, ArrayList<IrisVector> dataNormalized,
                      int learningPhases, double learningRate, double radius) {

        int mapHeight = mapWeights.size();
        int mapWidth = mapWeights.get(0).size();
        int inputSize = dataNormalized.get(0).getVectorSize();

        // Train on multiple phase ( also known as epoch )
        for (int learningPhase = 0; learningPhase < learningPhases; learningPhase++) {
            double learningRateForThisPhase = learningRate * (1 - (double) learningPhase / learningPhases);
            double radiusForThisPhase = radius * Math.exp(-(double) learningPhase / learningPhases);

            // Random sort for each phase
            ArrayList<Integer> indices = randomSort(dataNormalized.size());
            for (int indice : indices) {
                IrisVector pattern = dataNormalized.get(indice);

                // Find the BMU ( best match unit ) for this phase
                ArrayList<Integer> bmuIndex = findBMU(pattern, mapWeights);

                // Update neuron weight of the BMU neighbors
                for (int i = 0; i < mapHeight; i++) {
                    for (int j = 0; j < mapWidth; j++) {
                        //Calulate distance between neuron [i,j] and the BMU
                        double distance = euclideanDistance(i, j, bmuIndex.get(0), bmuIndex.get(1));
                        if (distance <= radiusForThisPhase) {
                            // Update of the neuron weight based on distance and learning rate
                            ArrayList<Double> neuron = mapWeights.get(i).get(j);
                            for (int k = 0; k < inputSize; k++) {
                                double newWeight = neuron.get(k) + learningRateForThisPhase * (pattern.getValue(k) - neuron.get(k));
                                neuron.set(k, newWeight);
                            }
                        }
                    }
                }
            }
        }
    }

    // Find BMU ( Best Match Unit ) for the given vector
    public ArrayList<Integer> findBMU(IrisVector irisVector, ArrayList<ArrayList<ArrayList<Double>>> mapWeights) {
        double minDistance = Double.MAX_VALUE;
        ArrayList<Integer> bmuIndice = new ArrayList<>();

        // Go across all the neuron of the map
        for (int i = 0; i < mapWeights.size(); i++) {
            for (int j = 0; j < mapWeights.get(i).size(); j++) {
                ArrayList<Double> neuron = mapWeights.get(i).get(j);
                double distance = euclideanDistance(irisVector, neuron);

                // Find the neuron with the lowest distance
                if (distance < minDistance) {
                    minDistance = distance;
                    bmuIndice.clear();
                    bmuIndice.add(i);
                    bmuIndice.add(j);
                }
            }
        }

        return bmuIndice;
    }

    private static double euclideanDistance(IrisVector pattern, ArrayList<Double> neuron) {
        double sum = 0.0;
        for (int i = 0; i < pattern.getVectorSize(); i++) {
            sum += Math.pow(pattern.getValue(i) - neuron.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    private static double euclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public ArrayList<String> getAllLabelOf(ArrayList<IrisVector> irisVectors) {
        ArrayList<String> labels = new ArrayList<>();
        for(IrisVector irisVector : irisVectors) {
            labels.add(irisVector.getLabel());
        }

        return labels;
    }

    private static ArrayList<Integer> randomSort(int size) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        return indices;
    }
}