import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Lexer {

    private String text;

    public Lexer(Scanner in){
        StringBuilder sb = new StringBuilder();
        while ( in.hasNextLine() )
            sb.append( in.nextLine() ).append(" ");
        text = sb.toString();
    }

    public Lexer(String in){
        text = in;
    }

    public List<Token> getTokens(){
        List<Token> tokens = new LinkedList<>();

        StringBuilder sb = new StringBuilder();
        TokenType savedType = null;

        for (int i = 0; i<text.length();){

            if ( (text.charAt(i) == ' ' || text.charAt(i) == '\n') && savedType == null ) {
                i++;
                continue;
            }

            sb.append(text.charAt(i));

            TokenType type = null;
            String str = sb.toString();
            for ( TokenType tokenType : TokenType.values() )
                if ( tokenType.test(str) )
                {
                    savedType = tokenType;
                    type = tokenType;
                    break;
                }

            // нужно вернуться на шаг назад
            if ( type == null && savedType != null ){
                sb.delete(sb.length()-1, sb.length());
                Token t = new Token(savedType, sb.toString());
                tokens.add(t);
                savedType = null;
                sb = new StringBuilder();
                continue;
            }

            i++;

        }

        // последний токен
        if ( savedType != null ){
            Token t = new Token(savedType, sb.toString());
            tokens.add(t);
        }

        return tokens;
    }

}
