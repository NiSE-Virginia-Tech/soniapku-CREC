#!/bin/bash
check_evn() {
 echo '|--------------------------------------------------------------------|'
 echo '|         checking necessary software                                |'
 echo '|--------------------------------------------------------------------|'

    if ! [ -x "$(command -v git)" ]; then
        echo 'Error : git not found!'
        echo 'Please install git first by typing: sudo apt-get install git'
        exit 1
    else
        echo 'Find git:'$(command -v git)
    fi
}

check_evn

basePath=$(pwd)
projectName=jfreechart

cd $basePath
echo '|--------------------------------------------------------------------|'
echo '|         git clone project repository from the Internet             |'
echo '|--------------------------------------------------------------------|'

if [ -d $basePath/$projectName ]; then
    rm -rf $basePath/$projectName
fi
	git clone https://github.com/jfree/jfreechart.git 

if [ ! -d $basePath/$projectName ]; then
    echo 'Git clone '$projectName' Failed! Please check the network connection!' 
    exit 1
else
    echo 'Already cloned '$projectName
fi

echo '|--------------------------------------------------------------------|'
echo '|         get all historical commit IDs                              |'
echo '|--------------------------------------------------------------------|'

commitsPath=$basePath/commits
mkdir $commitsPath
mkdir $commitsPath/$projectName
cd $basePath/$projectName
git log --pretty="%H,%P,%an,%cd,%s" --no-merges --first-parent master > ../commits/$projectName/authorCommit.txt

cd $basePath
java GetCommitsInfo $basePath/ $projectName
chmod +x $commitsPath/$projectName/run.sh
$commitsPath/$projectName/run.sh
date

echo '|--------------------------------------------------------------------|'
echo '|         sample versions                                            |'
echo '|--------------------------------------------------------------------|'

cd $basePath
java GetCloneInfo $basePath/ $projectName
chmod +x $commitsPath/$projectName/getCloneVersions.sh
$commitsPath/$projectName/getCloneVersions.sh 1>/dev/null
date

echo '|--------------------------------------------------------------------|'
echo '|         detect clones                                              |'
echo '|--------------------------------------------------------------------|'
projectPath=${basePath}/${projectName}Filter
tokenPath=${basePath}/SourcererCC/parser/java
sourcererPath=${basePath}/SourcererCC
propertyPath=$sourcererPath/sourcerer-cc.properties
fileList=`ls $projectPath`
for file in $fileList
do
	echo $file
	mkdir -p ${projectPath}/$file/token
	temp=$projectPath/$file/token
        temp=${temp//\//\\\/}
	echo "$temp"
	sed "s/query/${temp}/g" ${propertyPath}-template > $propertyPath
	cd $tokenPath
	java -jar InputBuilderClassic.jar $projectPath/$file/ $projectPath/$file/token/tokens.file $projectPath/$file/headers.file blocks java 30 0 6 0 false false false 8
	cd $sourcererPath
	java -jar dist/indexbased.SearchManager.jar index 8
	java -jar dist/indexbased.SearchManager.jar search 8
	cd $sourcererPath/output8.0
	cp tokensclones_index_WITH_FILTER.txt $projectPath/$file/
done
date

echo '|--------------------------------------------------------------------|'
echo '|         collect refactor clones                                    |'
echo '|--------------------------------------------------------------------|'
java -jar CollectRefactorClones.jar $basePath/ $projectName
date

echo '|--------------------------------------------------------------------|'
echo '|         extract features                                           |'
echo '|--------------------------------------------------------------------|'
java -jar ExtractFeatures.jar $basePath/ $projectName
date
