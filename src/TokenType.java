import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenType {

    ASSIGN_S("="),
    WHILE_KW("while"),
    IF_KW("if"),
    ELSE_KW("else"),
    VAR_KW("var"),
    PRINT_KW("print"),
    IDENTIFIER("[a-z]([0-9]|[a-z])*"),
    OPEN_ROUND_BRACKET("\\("),
    CLOSE_ROUND_BRACKET("\\)"),
    OPEN_CURLY_BRACKET("\\{"),
    CLOSE_CURLY_BRACKET("\\}"),
    //LITERAL("0|([-]?[1-9][0-9]*)"),
    LITERAL("0|([1-9][0-9]*)"),
    MINUS_S("-"),
    UN_MINUS("-"), // ВНИМАНИЕ! Lexer не может распознать этот токен. Это делает Parser!
    PLUS_S("\\+"),
    MULT_S("\\*"),
    DIV_S("/"),
    BIGGER_S(">"),
    LESS_S("<"),
    EQUALS_S("=="),
    NOT_S("!"),
    AND_S("\\&"),
    OR_S("\\|"),
    SEMICOLON(";");

    private Pattern pattern;

    TokenType(String regex){
        pattern = Pattern.compile( regex );
    }

    public boolean test(String str){
        Matcher m = pattern.matcher(str);
        return m.matches();
    }

}
