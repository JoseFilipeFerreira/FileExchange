package Server;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class User {
    private String name;
    private String passwd;

    User(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    boolean checkPasswd(String passwd) {
        return this.passwd.equals(passwd);
    }

    String Serialize() {
        return "User:name='" + this.name
                + "':passwd='" + this.passwd + "';";
    }

    static Optional<Object> deserialize(String s) {
        Pattern regexr = Pattern.compile("User:name='(?<name>\\w)':passwd='(?<passwd>\\w)';");
        Matcher e = regexr.matcher(s);
        if(e.matches())
            return Optional.of(new User(e.group("name"), e.group("passwd")));
        return Optional.empty();
    }
}
