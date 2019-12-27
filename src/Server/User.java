package Server;

import Utils.Result;

import java.util.regex.Matcher;

class User {
    private String name;
    private String passwd;

    User(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    boolean check_passwd(String passwd) {
        return this.passwd.equals(passwd);
    }

    String get_name() {
        return this.name;
    }

    public String toString() {
        return "User{name='" + this.name
                + "':passwd='" + this.passwd + "'}";
    }

    static Result<User, String> from_string(String s) {
        Matcher e = ParserPatterns.user.matcher(s);
        if(e.matches())
            return Result.Ok(new User(e.group("name"), e.group("passwd")));
        return Result.Err("Invalid user format");
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return user.name.equals(name) &&
                user.passwd.equals(passwd);
    }
}
