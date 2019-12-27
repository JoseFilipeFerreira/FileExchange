package Server;

import java.util.regex.Pattern;

class ParserPatterns {
    static Pattern user = Pattern.compile("User\\{name='(?<name>.*)'" +
                                                  ":passwd='(?<passwd>.*)'}");

    static Pattern music = Pattern.compile("Music\\{title='(?<title>.*)'" +
                                             ":artist='(?<artist>.*)'" +
                                             ":year=(?<year>[0-9]{4})" +
                                             ":tags=\\[(?<tags>.*)]}");

    static Pattern list_content = Pattern.compile("'(\\w*)',");

    static Pattern requests = Pattern.compile("\\{type='(?<type>.*)', content=\\[(?<content>.*)]}$");

    static Pattern list_objetcs = Pattern.compile("'(.*)',");
}
