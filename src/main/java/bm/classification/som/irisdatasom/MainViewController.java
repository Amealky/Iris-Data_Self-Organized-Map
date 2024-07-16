package bm.classification.som.irisdatasom;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import static java.lang.Double.max;

public class MainViewController implements Initializable {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    Map<String, Object> settings = new HashMap<>();
    ArrayList<IrisVector> irisVectors = new ArrayList<>();
    ArrayList<IrisVector> irisVectorsToPredict = new ArrayList<>();
    public double[][][] colorMap;
    SOM som;

    @FXML
    private StackPane rootStackPane;

    @FXML
    private AnchorPane anchorPaneAppLoading;

    @FXML
    private AnchorPane anchorPaneAppLoaded;

    @FXML
    private GridPane gridSOMVisualization;

    @FXML
    private Rectangle rectangleIrisSetosa;

    @FXML
    private Rectangle rectangleIrisVersicolor;

    @FXML
    private Rectangle rectangleIrisVirginica;

    @FXML
    private Button buttonRestartLearning;

    @FXML
    private Button buttonLoadDataToPredict;

    @FXML
    private TextField textFieldDataToPredictFile;

    @FXML
    private Button buttonStartPrediction;

    @FXML
    private TextFlow textFlowDataToPredict;

    @FXML
    private ProgressIndicator progressIndicatorDataToPredict;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        settings = FileLoader.readSimpleJsonFile("settings/app_settings.json");
        startLearningTask();
    }

    @FXML
    public void onButtonRestartLearningClicked() {
        clearAllPredictedLabels();
        textFlowDataToPredict.getChildren().clear();
        fillDataToPredictTextFlow();
        startLearningTask();
    }

    @FXML
    public void onButtonLoadDataToPredictClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select data to predict file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Data Files", "*.data"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            irisVectorsToPredict.clear();
            irisVectorsToPredict = FileLoader.loadIrisDatasFile(file.getAbsolutePath());
            textFieldDataToPredictFile.setText(file.getName());
            buttonStartPrediction.setDisable(false);
            fillDataToPredictTextFlow();
        }

    }

    @FXML
    public void onButtonStartPredictionClicked(ActionEvent event) {
        startPredictionTask();
    }

    private void startLearningTask() {
        showAppLoading();
        Task<Void> learningTask = new Task<>() {
            @Override
            protected Void call() {
                launchLearning();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                hideAppLoading();
                generateKohonenMapUI();
                setLegendColors();
            }
        };
        executorService.submit(learningTask);
    }

    public void launchLearning() {
        settings = FileLoader.readSimpleJsonFile("settings/app_settings.json");
        int mapWidth = (int) settings.get("mapWidth");
        int mapHeight = (int) settings.get("mapHeight");
        int learningPhases = (int) settings.get("learningPhases");
        double learningRate = (double) settings.get("learningRate");

        double radius = max(mapWidth, mapHeight) / 2;

        som = new SOM();
        irisVectors = FileLoader.loadIrisDatasFile("datas/iris.data");
        som.calculateAverageVector(irisVectors);
        som.calculateStdDevVector(irisVectors);
        ArrayList<IrisVector> normalizedDatas = som.normalizeData(irisVectors);
        int vectorSize = normalizedDatas.get(1).getVectorSize();
        som.initializeWeightMap(mapHeight, mapWidth, vectorSize);
        som.learn(som.weightMap, normalizedDatas, learningPhases, learningRate, radius);
    }

    private void startPredictionTask() {
        showDataToPredictLoading();
        Task<Void> predictionTask = new Task<>() {
            @Override
            protected Void call() {
                launchPrediction();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                hideDataToPredictLoading();
                fillDataToPredictTextFlow();
            }
        };
        executorService.submit(predictionTask);
    }


    public void launchPrediction() {
        ArrayList<IrisVector> normalizedVectors = som.normalizeData(irisVectorsToPredict);
        for (IrisVector irisVector : normalizedVectors) {
            ArrayList<Integer> bmuIndice = som.findBMU(irisVector, som.weightMap);

            // Use color map to determine the vector appartenance ( easier )
            double[] bmuColor = colorMap[bmuIndice.get(0)][bmuIndice.get(1)];
            String predictedClass = determineClass(bmuColor);

            irisVector.setLabel(predictedClass);
        }
        for(int i = 0; i < normalizedVectors.size(); i++) {
            irisVectorsToPredict.get(i).setLabel(normalizedVectors.get(i).getLabel());
        }
    }

    private String determineClass(double[] bmuColor) {
        if (Arrays.equals(bmuColor, Utils.hexToRGBA((String) settings.get("iris-setosa-color")))) {
            return "iris-setosa";
        } else if (Arrays.equals(bmuColor,  Utils.hexToRGBA((String) settings.get("iris-versicolor-color")))) {
            return "iris-versicolor";
        } else if (Arrays.equals(bmuColor,  Utils.hexToRGBA((String) settings.get("iris-virginica-color")))) {
            return "iris-virginica";
        }
        return "unknown";
    }

    //***************
    //      UI
    //***************

    public void generateKohonenMapUI() {
        int mapWidth = som.weightMap.get(0).size();
        int mapHeight = som.weightMap.size();


        colorMap = new double[mapHeight][mapWidth][3];

        // Init color map with unknown color define in settings
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                colorMap[i][j] = Utils.hexToRGBA((String) settings.get("unknown-color"));
            }
        }


        for (int indice = 0; indice < som.normalizeData(irisVectors).size(); indice++) {
            IrisVector currentNormalizedIrisVector = som.normalizeData(irisVectors).get(indice);
            ArrayList<Integer> bmuIndice = som.findBMU(currentNormalizedIrisVector, som.weightMap);

            ArrayList<String> labels = som.getAllLabelOf(irisVectors);
            // Update the colors map regarding the label
            if (labels.get(indice).equals("iris-setosa")) {
                colorMap[bmuIndice.get(0)][bmuIndice.get(1)] = Utils.hexToRGBA((String) settings.get("iris-setosa-color"));
            } else if (labels.get(indice).equals("iris-versicolor")) {
                colorMap[bmuIndice.get(0)][bmuIndice.get(1)] =  Utils.hexToRGBA((String) settings.get("iris-versicolor-color"));
            } else if (labels.get(indice).equals("iris-virginica")) {
                colorMap[bmuIndice.get(0)][bmuIndice.get(1)] = Utils.hexToRGBA((String) settings.get("iris-virginica-color"));
            }
        }


        double cellWidth = gridSOMVisualization.getWidth() / mapWidth;
        double cellHeight = gridSOMVisualization.getHeight() / mapHeight;

        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                double[] color = colorMap[i][j];

                Rectangle rectangle = new Rectangle(cellWidth, cellHeight);
                rectangle.setFill(Color.rgb((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255)));
                rectangle.setStroke(Color.BLACK);

                gridSOMVisualization.add(rectangle, j, i);
            }
        }
    }

    public void setLegendColors() {
        rectangleIrisSetosa.setFill(Color.valueOf((String) settings.get("iris-setosa-color")));
        rectangleIrisVersicolor.setFill(Color.valueOf((String) settings.get("iris-versicolor-color")));
        rectangleIrisVirginica.setFill(Color.valueOf((String) settings.get("iris-virginica-color")));
    }

    public void fillDataToPredictTextFlow() {
        textFlowDataToPredict.getChildren().clear();
        for (IrisVector irisVector : irisVectorsToPredict) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" ");
            for (Double value : irisVector.getValues()) {
                stringBuilder.append(value).append(", ");
            }
            Text valuesText = new Text(stringBuilder.toString());
            valuesText.setStyle("-fx-fill: #25d00b;");

            Text typeText = new Text("type => ");
            typeText.setStyle("-fx-fill: white");
            String labelColor = (String) settings.get(irisVector.getLabel() + "-color");
            Text labelText = new Text(irisVector.getLabel());
            labelText.setStyle("-fx-fill: " + labelColor + ";");

            textFlowDataToPredict.getChildren().addAll(valuesText, typeText, labelText, new Text("\n"));
        }
    }


    public void clearAllPredictedLabels() {
        for(IrisVector irisVector : irisVectorsToPredict) {
            irisVector.setLabel("unknown");
        }
    }

    public void showAppLoading() {
        anchorPaneAppLoaded.setVisible(false);
        anchorPaneAppLoading.setVisible(true);
    }

    public void hideAppLoading() {
        anchorPaneAppLoaded.setVisible(true);
        anchorPaneAppLoading.setVisible(false);
    }

    public void showDataToPredictLoading() {
        textFlowDataToPredict.setVisible(false);
        progressIndicatorDataToPredict.setVisible(true);
    }

    public void hideDataToPredictLoading() {
        textFlowDataToPredict.setVisible(true);
        progressIndicatorDataToPredict.setVisible(false);
    }


}