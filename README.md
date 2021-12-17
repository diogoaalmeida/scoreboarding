# Scoreboarding

```sh
java -version
openjdk version "1.8.0_302"
OpenJDK Runtime Environment Corretto-8.302.08.1 (build 1.8.0_302-b08)
OpenJDK 64-Bit Server VM Corretto-8.302.08.1 (build 25.302-b08, mixed mode)

# Compile
rm -rf bin && mkdir bin && javac -d bin  -sourcepath scoreboarding/src/  scoreboarding/src/br/com/scoreboarding/*.java

# Run with the parameter=> java -cp ./bin br.com.scoreboarding.Main [CAMINHO]
java -cp ./bin br.com.scoreboarding.Main scoreboarding/src/br/com/scoreboarding/tests/test.asm
```
