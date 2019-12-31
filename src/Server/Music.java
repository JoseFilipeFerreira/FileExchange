package Server;

import Utils.ParserPatterns;
import Utils.Result;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

class Music {
    private long id;
    private String title;
    private String artist;
    private Year year;
    private List<String> tags;
    private long downloads;

    private static Counter last_id = new Counter();

    Music(String title, String artist, Year year, List<String> tags) {
        this.id = last_id.increment();
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.tags = tags;
        this.downloads = 0;
    }

    synchronized void download() {
        this.downloads++;
    }

    String get_path() {
        return ".media/" + this.id;
    }

    long get_id() {
        return this.id;
    }

    String get_title() {
        return this.title;
    }

    String get_artist() {
        return this.artist;
    }

    Year get_year() {
        return this.year;
    }

    List<String> get_tags() {
        return this.tags;
    }

    boolean contains_tag(String tag) {
        return this.tags.contains(tag);
    }

    public String toString() {
        String all_tags = "";
        for(String tag: this.tags)
            all_tags += "'" + tag + "',";
        return "Music{id='" + this.id
                + "':artist='" + this.artist
                + "':year='" + this.year
                + "':tags=[" + all_tags + "]}";
    }

    static Result<Music, String> from_string(String s) {
        Matcher e = ParserPatterns.music.matcher(s);
        if(e.matches()) {
            String tags = e.group("tags");
            Matcher o = ParserPatterns.list_content.matcher(tags);
            List<String> tag = new ArrayList<>();
            while(o.find())
                tag.add(o.group(1));
            return Result.Ok(new Music(e.group("title"),
                                         e.group("artist"),
                                         Year.parse(e.group("year")),
                                         tag));
        }
        return Result.Err("Invalid Format Music");
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return this.title.equals(music.title) &&
                this.artist.equals(music.artist);
    }
}
