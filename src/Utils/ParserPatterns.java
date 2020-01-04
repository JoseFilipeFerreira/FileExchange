package Utils;

import java.util.regex.Pattern;

public class ParserPatterns {
    public final static Pattern user = Pattern.compile("User\\{name='(?<name>.*)'" +
                                                  ":passwd='(?<passwd>.*)'}");

    public final static Pattern music = Pattern.compile("Music\\{title='(?<title>.*)'" +
                                             ":artist='(?<artist>.*)'" +
                                             ":year=(?<year>[0-9]{4})" +
                                             ":tags=\\[(?<tags>.*)]}");

    public static Pattern complete_music = Pattern.compile("Music\\{id='(?<id>[0-9]+)'" +
                                                                   ":title='(?<title>.*?)'" +
                                                                   ":artist='(?<artist>.*?)'" +
                                                                   ":year=(?<year>[0-9]{4})" +
                                                                   ":downloads=(?<downloads>[0-9]+)" +
                                                                   ":tags=\\[(?<tags>.*)]}");

    public final static Pattern list_content = Pattern.compile("'(\\w*)',");

    public final static Pattern requests = Pattern.compile("\\{type='(?<type>.*)', content=\\[(?<content>.*)]}$");

    public static Pattern list_objetcs = Pattern.compile("'(.*?)';");

    public final static Pattern replies = Pattern.compile("\\{status='(?<status>.*)', result='(?<result>.*)'}");
}
