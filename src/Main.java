
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static private final boolean PRINT_TOKENS = false;
    static private final boolean PRINT_COMMANDS = false;

    public static void main(String[] args) {

        try {
            Scanner in = new Scanner(new File("main.txt"));
            Lexer lexer = new Lexer(in);
            List<Token> tokens = lexer.getTokens();
            if ( PRINT_TOKENS )
                System.out.println(tokens);
            Parser parser = new Parser(tokens);
            ArrayList<Command> cmds = parser.compile();
            if ( PRINT_COMMANDS )
                System.out.println(cmds);
            Interpreter interpreter = new Interpreter(cmds);
            interpreter.exec();
        } catch (FileNotFoundException ex){
            System.out.println("missing file");
        } catch ( ParserException ex ){
            System.out.println( "Parser exception: " + ex.getMessage() );
        }

    }
}
