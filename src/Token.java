
import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value){
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        if ( type == null )
            throw new RuntimeException("token has no type???");
        return type;
    }

    @Override
    public int hashCode(){
        return Objects.hash(type, value);
    }

    @Override
    public boolean equals(Object obj){
        if ( obj == null ) return false;
        if ( obj.getClass() != this.getClass() ) return false;
        Token token = (Token) obj;
        return Objects.equals(getValue(), token.getValue()) && token.getType() == this.getType();
    }

    @Override
    public String toString(){
        return "<" + type.toString() + ", " + value + ">";
    }

}
