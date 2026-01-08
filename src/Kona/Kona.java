package Kona;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Kona {
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: kona [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            // todo: display menu
        }
    }

    private static void runFile(String path) throws IOException {
        if (hadError) System.exit(65);
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        System.out.println(tokens);

        Parser parser = new Parser(tokens);
        Stmt statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        System.out.println(new AstPrinter().print(statements));   // book says replace line with: interpreter.interpret(statements);
        // System.out.println(new ZigOutput().print(statements));
    }

    static void error(int line, int column, int length, String message) {
        report(line, column, length, message);
    }

    // todo: show error lines
    private static void report(int line, int column, int length, String message) {
        System.err.println(line + ":" + column + " error: " + message);
        hadError = true;
    }

    private static void reportEOF(int line, String message) {
        System.err.println("line: " + line + ", error: end of file, " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            reportEOF(token.line, message);
        } else {
            report(token.line, token.column, token.length, message);
        }
    }
}