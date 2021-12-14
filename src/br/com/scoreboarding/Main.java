package br.com.scoreboarding;

import br.com.scoreboarding.memory.Memory;
import br.com.scoreboarding.processor.Processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> tests = Arrays.asList(
                "src/br/com/scoreboarding/tests/test.asm",
                "src/br/com/scoreboarding/tests/test_1E.asm",
                "src/br/com/scoreboarding/tests/test_raw.asm",
                "src/br/com/scoreboarding/tests/test_war.asm",
                "src/br/com/scoreboarding/tests/test_waw.asm"
        );
        if (args.length == 0) {
            throw new RemoteException("Parâmetros Inválidos!");
        }
        for (String s : args) {
            Path path = Paths.get(s);
            List<String> lines = Files.lines(path).collect(Collectors.toList());
            Memory memory = new Memory(lines);
            Processor processor = new Processor(path, memory, lines);
            processor.clock();
        }
    }
}
