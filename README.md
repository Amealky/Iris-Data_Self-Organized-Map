
<h1 align="center">🤖 IrisData-Self-Organized-Map 🧠</h1>
<h3 align="center"> Machine Learning </h3>
<p align="center">
   <a href="https://fr.wikipedia.org/wiki/Java_(langage)"> 
        <img src="https://img.shields.io/badge/Java-%204--2--1?style=for-the-badge&label=language&color=red">
    </a>
    <a href="https://fr.wikipedia.org/wiki/FXML"> 
        <img src="https://img.shields.io/badge/17--0--6-%204--2--1?style=for-the-badge&label=FXML%20Version&color=Greylogo=fxml&logoColor=white">
    </a>
  
</p>
<p align="center">
 <img  src="https://i.ibb.co/9mx0jH7/ezgif-2-b46c2aa731.gif" width=500 height=313>
</p>

## Overview
This project is a GUI application made with ``FXML``, it allow the user to visualize and predict iris datas using a Self-Organizing Map (SOM)
and the famous ``iris.data`` dataset as learning base.

The app learn and categorize iris datas using the file ``datas/iris.data`` and show you a kohonen map of ``neurons`` ( gray square neurons are dead ones )

Then you can use the ``datas/iris_to_predict.data`` to try the prediction ( or create your own ``{whatever_iris}.data`` )

## Features
1 - ``Play with learning variables on fly`` by modifying the following file ``settings/app_settings.json`` you can for example modify number of ``learning_phases`` or the ``learning_rate``, Rerun the learning phase to take your modification into account

2 - ``Modify neuron categorizing colors on fly`` by modifying the following file ``settings/app_settings.json`` you can play with ``iris-setosa-color`` ``iris-versicolor-color`` or ``iris-virginica-color``, Rerun the learning phase to take your modification into account

3 - ``Predict iris datas`` you can load a non-labeled ``.data`` file like the one used for example in ``datas/iris_to_predict.data`` then ask the program to predict the iris type for each vector in the file

## Run it
The github file provide a file named ``IrisData-SOM-1.0-SNAPSHOT.jar`` you can run it with the java command here : ``java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar IrisData-SOM-1.0-SNAPSHOT.jar``

BUT the prefered way is to use maven (``mvn``) and just do at the root project folder ``mvn javafx:run``

## Build the project
If you want to build the project you can follow the steps below :

- If you just want to build an exported jar install maven ( mvn ) and run mvn clean package at the root project folder and skip the step belows !
- Ensure you have Java installed ( used version for this rebuilt is ``java coretto-11`` )
- Download [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/other.html) 
- Open the project using IntelliJ
- Make a run config using ``IrisDataSomApplication`` as main class and set java version to ``java coretto-11``
- Then you just have to press build for building the project or run to run it

Note : Build with Java 11 and based on FXML 17.0.6
