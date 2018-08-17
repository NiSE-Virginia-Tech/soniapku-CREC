# CREC

## I. Introduction

This project is built to automatically recommend clones for refactoring, which leverages both the present status and the past evolution of clones. 

* This project is implemented for the research paper “Automatic Clone Recommendation for Refactoring Based on the Present and the Past” accepted by [ICSME 2018](https://icsme2018.github.io/).*

## II. Environment

* OS: GNU Linux (Tested on Ubuntu 14.04 LTS) 
* JDK: JDK1.8

## III. How to run

In the runnable folder, there is a shell scipt named as "runCREC.sh". It contains the following steps:

* Check necessary software to make sure you have installed git.
* Git clone a project repository from the Internet (git clone JFreeChart's repository by default).
* Get all historical commit IDs.
* Sample versions.
* Detect clones for sampled versions.
* Collect refactored clones.
* Extract features of refactored clones.

To collect refactored clones and extract features for other projects, please change the projectName and project repository settings in the script.

*CREC* was developed as an [Eclipse](http://www.eclipse.org/mars/) Java Project. If you want to view the source code, you can simply **import** the project into your workspace(in the code folder).

## IV.  Structure of the project
```powershell
  |--- README.md           :  user guidance
  |--- codes               :  source code
  |--- runnable            :  shell for preparation
  |--- features            :  features extracted by CREC
  |--- refactorInstances   :  refactor instances collected by CREC
  |--- unrefactorInstances :  not refactor instances collected by CREC
  |--- results             :  machine learning results in paper

```

----


<u> All suggestions are welcomed.</u>
