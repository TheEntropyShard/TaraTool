# TaraTool
TaraTool is a small and simple command line utility for packing/unpacking ```*.tara``` files. <br>
Tara file is a simple archive format created by Alternativa Games. It was used to create prop libraries for Tanki Online.

### Quick start
```shell
git clone https://github.com/TheEntropyShard/TaraTool.git
cd TaraTool
mvn clean compile assembly:single
java -jar target/TaraTool.jar
```
Then you will see usage:
```
No args specified
Usage: java -jar TaraTool.jar <mode> <input file/folder> <output file/folder>
  Modes:
    pack - Pack tara
      Args: <input folder> <output file>
    unpack - Unpack tara
      Args: <input file> <output folder>
```
