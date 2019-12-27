package Server;

import Utils.Result;

import java.util.HashMap;
import java.util.Map;

class MusicCenter {
    private Map<Long, Music> musics;
    private Map<String, User> users;

    MusicCenter() {
        this.musics = new HashMap<>();
        this.users = new HashMap<>();
    }

    Result<User, String> create_user(User u) {
        if(this.users.containsKey(u.get_name()))
            return Result.Err("User already exists");
        this.users.put(u.get_name(), u);
        return Result.Ok(u);
    }

    Result<User, String> check_login(User u) {
        return Result.of_nullable(this.users.get(u.get_name()), "Invalid User")
                .and_then(x -> x.equals(u)
                        ? Result.Ok(x)
                        : Result.Err("Invalid Passwd"));
    }

    String search_tags(String tag) {
        StringBuilder res = new StringBuilder().append("[");
        this.musics.values()
                .stream()
                .filter(x -> x.contains_tag(tag))
                .forEach(x -> res.append("'")
                        .append(x.toString())
                        .append("',"));
        return res.append("]").toString();
    }

    //TODO Some nice file managment
    Result<Music, String> upload_music(Music a) {
        if(this.musics.containsValue(a))
            return Result.Err("Music already exists");
        this.musics.put(a.get_id(), a);
        return Result.Ok(a);
    }
}
