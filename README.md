# Variant Drift - Prototype and Replication Package

## 1. Overview


This package is directly related to the following SPLC2020 contribution:

Alexander Schultheiß, Paul Maximilian Bittner, Timo Kehrer, and Thomas
Thüm. 2020. On the Use of Product-Line Variants as Experimental Subjects
for Clone-and-Own Research: A Case Study. In 24th ACM International
Systems and Software Product Line Conference (SPLC ’20), October 19–23,
2020, MONTREAL, QC, Canada. ACM, New York, NY, USA, 6 pages. 
https://doi.org/10.1145/3382025.3414972

It contains all data and code required in order to replicate the experimental evaluation presented in the paper. 
In addition, the provided tools can be used to apply the proposed variant drift to new datasets, as well as for evaluation of different matchers on the generated variants.  

## 2. Abstract

Software is often released in multiple variants to address the needs
of different customers or application scenarios. One frequent approach
to creating newvariants is clone-and-own, whose systematic
support has gained considerable research interest in the last decade.
However, only few techniques have been evaluated in a realistic
setting, due to a substantial lack of publicly available clone-andown
projects which could be used as experimental subjects. Instead,
many studies use variants generated from software product lines
for their evaluation. Unfortunately, the results might be biased, because
variants generated from a single code base lack unintentional
divergences that would have been introduced in clone-and-own
development. In this paper, we report about ongoing work towards
a more systematic investigation of potential threats to the external
validity of such experimental results. Using n-way model matching
as selected yet representative technique for supporting clone-andown,
we assess the performance of state-of-the-art algorithms on
variant sets exposing increasing degrees of divergences. We compile
our observations into four hypotheses which are meant to serve as
a basis for discussion and which need to be investigated in more
detail in future research.

Visit https://doi.org/10.1145/3382025.3414972 for the full publication.

## 3. Project Structure

```
VariantDrift
|- data
|- VariantDriftEvaluation
|- VariantDriftPrototype
|- reported_data.zip
```

### 3.1 Directory: data
When running the tools "VariantDriftPrototype" and "VariantDriftEvaluation", provided in this package, all data generated by the tools is stored in the 'data' directory. Initially, the directory contains 'experimental_subjects', a sub-directory in which the csv files of the experimental subjects are located. 

### 3.2 Directory: VariantDriftEvaluation
This is a Python project containing the scripts that were used to evaluate the experimental results. We recommend to use [Pycharm](https://www.jetbrains.com/pycharm/) as IDE for opening the project. ```You should open this directory as a project in your Python IDE, so that the directory 'VariantDriftEvaluation' is the root of the project. You can find detailed instructions in the Setup sections below.```

### 3.3 Directory: VariantDriftPrototype
This is the Java prototype containing all code related to the variant generation using variant drift, and the running of the experiments by applying different matching algorithms to generated variants. We recommend to use [IntelliJ](https://www.jetbrains.com/idea/download) as IDE. ```You should open this directory as a project in your Java IDE, so that the directory 'VariantDriftPrototype' is the root of the project. You can find detailed instructions in the Setup sections below.```

### 3.4 File: reported_data.zip
This archive contains all experimental subjects, generated variants, experimental results, and generated plots, as presented in our submission. ```Please unpack it into the root directory of the cloned repository:``` 

```
VariantDrift
|- data
|- VariantDriftEvaluation
|- VariantDriftPrototype
|- reported_data
|- reported_data.zip
```

## 4. Setup Prerequisites

What things you need to install the software and how to install them:

* Java [JDK-14](https://jdk.java.net/14/) or higher is configured in your IDE
* [Python 3.8](https://www.python.org/downloads/release/python-380/) or higher ; older 3.x versions might work
* A suitable Java IDE, such as [IntelliJ](https://www.jetbrains.com/idea/download) OR [Eclipse](https://www.eclipse.org/downloads/) for Java developers with [Maven](https://www.eclipse.org/m2e/) Plugin. Both IDEs should be set up to use Java 14 or higher.
* A suitable Python IDE, such as [PyCharm](https://www.jetbrains.com/pycharm/download/) 
* [Matplotlib](https://matplotlib.org/)[1] for python is installed on your system. You can install matplotlib by either following the official [installation instructions](https://matplotlib.org/users/installing.html) or by installing the package directly via PyCharm according to the following [package installation instructions](https://www.jetbrains.com/help/pycharm/installing-uninstalling-and-upgrading-packages.html) for PyCharm. 

## 5. Setup of the VariantDriftPrototype Project in a Java IDE

The following contains instructions describing how to set up the prototype using Intellij or Eclipse 

### 5.1 Project Setup IntelliJ - Variant Drift Prototype

How to get the prototype up and running - a short description.

If you are in the WELCOME screen of IntelliJ:

```
Open > Select the directory which contains the prototype: ${VariantDrift}/VariantDriftPrototype > Press Ok
```

If you already have another project open:

```
File > Open > Select the path to ${VariantDrift}/VariantDriftPrototype > Press Ok
```

### 5.2 Project Setup Eclipse - Variant Drift Prototype
Start Eclipse and open a workspace.
```
File > Open Projects from File System > Select the ${VariantDrift}/VariantDriftPrototype directory > Finish
```

## 6. Setup of the VariantDriftEvaluation Project in PyCharm
This section describes how you can set up the phyton project used to evaluate the matching results.

If you are in the WELCOME screen of PyCharm:

```
Open > Select the directory which contains the prototype: ${VariantDrift}/VariantDriftEvaluation > Press Ok
```

If you already have another project open:

```
File > Open > Select the path to ${VariantDrift}/VariantDriftEvaluation > Press Ok
```

## 7. Replicating the Drifting of Variants
### 7.1 Prerequisites
* The VariantDriftPrototype has been opened as a project in your Java IDE as described above
* A JDK with version 14 or higher is registered in your Java IDE and set up for the project. 
 
```Setting up the correct JDK is important, because the prototype uses switch expressions and Maven might not load the project correctly. If you get any build errors, make sure that the correct JDK is set up for the project and Maven in your IDE. Afterwards, try to reload the project with Maven.``` 

### 7.2 Steps for Drifting Variants
You can replicate the generation of variants as described in the paper by following these steps: 
* Open the src/main/java/de.hub.mse.variantsync.variantdrift.refactoring.DatasetRefactoring.java file in your IDE
* Right-click on the DatasetRefactoring.java file > Run 'DatasetRefactoring.main()'
  
```Please note that the generation of variants can take several minutes or hours depending on the variables in the DatasetRefactoring.java file. The provided values will repeat the same setup as presented in the paper.```

After the generation of variants has been completed you will find a new directory 'refactored_subjects' in the 'data' directory. Here, the generated variants are stored as .csv files. Each variant has its own file following the name pattern '{_SubjectName_}\_{_RefactoringDistribution_}\_{_NumberOfRefactorings_}\_{_VariantId_}.csv'. For example, 'Apogames_Known_300_0.csv' contains the a variant with __id 0__ generated by applying __300__ refactoring operations to the __Apo-Games__ subject, using a __known__ distribution of refactorings (see Table 1 in the paper).

## 8. Replicating the Matching of Drifted Variants
We now describe how you can run the implementations of the different matching algorithms on the refactored variants. 

### 8.1 Prerequisites
* The VariantDriftPrototype has been opened as a project in your Java IDE as described above
* A JDK with version 14 or higher is registered in your Java IDE and set up for the project. 
  
```Setting up the correct JDK is important, because the prototype uses switch expressions and Maven might not load the project correctly. If you get any build errors, make sure that the correct JDK is set up for the project and Maven in your IDE. Afterwards, try to reload the project with Maven.``` 

### 8.2 Steps for Matching Drifted Variants
You can follow these steps if you want to reproduce the experiments in your IDE. 
* Unpack the reported_data.zip into the root directory of the _replication_ _package_, i.e., the parent directory of the VariantDriftPrototype 
* Open the experiment runner located under de.hub.mse.variantsync.variantdrift.experiments.variantdrift.ExperimentRunner.java
* Change the value of 'baseDatasetDir' in Line 17 to "./../reported_data/refactored_subjects". If there are any problems with the files not being found, check whether the archive has been unpacked correctly, or specify the absolute path to the directory. ```You can skip this step if you instead want to use the variants generated by running the dataset refactoring as described in Section 7.```
* Right-click on the ExperimentRunner.java file > Run 'ExperimentRunner.main()'. 
```Please note that running the different matchers for all variants can take several days, depending on your machine. This is due to the high runtime of NwM. We recommend disabling NwM in Line 26 for a faster calculation using only the other matchers.```

## 9. Replicating the Evaluation of Matching Results
We now describe how you can evaluate the matching results using the VariantDriftEvaluation project.

### 9.1 Prerequisites
* The VariantDriftEvaluation has been opened as a project in your Python IDE as described above
* Python with version 3.8 or higher is registered in your Python IDE and set up for the project  
* [Matplotlib](https://matplotlib.org/) for python [1] is installed on your system. See Section 4 for installation instructions.

### 9.2 Steps for Generating Result Plots 
You can follow these steps if you want to generate the plots presented in the paper. 
* Unpack the reported_data.zip into the root directory of the _replication_ _package_, i.e., the parent directory of the VariantDriftEvaluation 
* Open the '__main__.py' file located under 'VariantDriftEvaluation/src/main.py'
* Change the value of 'data_directory' in Line 6 to "./../../reported_data/experimental_results". If there are any problems with the files not being found, check whether the archive has been unpacked correctly, or specify the absolute path to the directory. ```You can skip this step if you instead want to evaluate the results of your own experimental runs (Section 8).```
* Right-click on the __main__.py file > Run...'. 

## 10. Running the Prototypes on Your Own Datasets

### 10.1 Prerequisites
* The VariantDriftPrototype has been opened as a project in your Java IDE as described above
* A JDK with version 14 or higher is registered in your Java IDE and set up for the project 
* Your dataset is stored in .csv files and follows the required format described in 10.2 

### 10.2 Expected Dataset Format
The models of each dataset are stored in a single csv-file, where each line in the file describes exactly one element of a model. Each line in the csv file describes an element by first specifying the model it belongs to, followed by the UUID of the element, the element's name, and the element's properties consisting of at least the name property which always has to start with "n_". All values should be separated by comma, so make sure that the values themselves do not include one. 

The properties are specified as a list of values separated by semicolon, where each value is unique. Because some elements might have an element twice, we chose to add an additional number to each property: _PropertyName\_i_. The number _i_ specifies that this is the _i-th_ occurrence of this property in this element. 


```
${Model-ID},${Element-UUID},${ElementName},n_${ElementName};${PropertyA}_1;${PropertyB}_1;${PropertyA}_2
```

The following example represents a slice of our experimental subject __bCMS__.
```
bCMS01,_1Mnu7WndEeWZOoTCcU_ksg,Witness,n_witness_1;policeOfficers_1;firemen_1
bCMS01,_1MnvrGndEeWZOoTCcU_ksg,Route,n_route_1;ETA_1;path_1
bCMS02,_1MnuumndEeWZOoTCcU_ksg,BCMSSystem,n_bCMSSystem_1;govermentAgency_1;govermentAgency_2
bCMS03,_1MnuumndEeWZOoTCcU_ksg,BCMSSystem,n_bCMSSystem_1;govermentAgency_1
```

### 10.3 Running the Prototypes
You can simply follow these steps for repeating the experiments described in the paper for your own dataset:
- Save the csv file in which the elements of the dataset are described in the 'data/experimental_subjects' directory
- Adjust line 64 of the DatasetRefactoring.java class file by including your dataset in the list
- Follow the steps in Section 7
- Adjust line 39 of the ExperimentRunner.java class file by including your dataset in the list
- Follow the steps in Section 8
- Adjust line 22 of the __main__.py python script by including your dataset in the list
- Follow the steps in Section 9

## References and Links
* [1] [Matplotlib](https://matplotlib.org/) -- Thomas A Caswell, Michael Droettboom, Antony Lee, John Hunter, Eric Firing, Elliott Sales de Andrade, … Paul Ivanov. (2020, July 16). matplotlib/matplotlib: REL: v3.3.0 (Version v3.3.0). Zenodo. http://doi.org/10.5281/zenodo.3948793
* [Maven](https://maven.apache.org/) - Dependency Management
* [Python 3.8](https://www.python.org/downloads/release/python-380/)
* [JDK-14](https://jdk.java.net/14/)
* [IntelliJ](https://www.jetbrains.com/idea/download) 
* [Pycharm](https://www.jetbrains.com/pycharm/download)
* [Eclipse](https://www.eclipse.org/downloads/) with [Maven](https://www.eclipse.org/m2e/) Plugin