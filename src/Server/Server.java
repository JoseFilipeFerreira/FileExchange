package Server;

import Utils.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Server {
    private Map<Long, Music> musics;
    private Map<String, User> users;

    Server() {
        this.musics = new HashMap<>();
        this.users = new HashMap<>();
    }

    Result<User, String> create_user(String name, String passwd) {
        if(this.users.containsKey(name))
            return Result.Err("User already exists");
        User n = new User(name, passwd);
        this.users.put(name, n);
        return Result.Ok(n);
    }

    Result<User, String> check_login(String name, String passwd) {
        return Result.of_nullable(this.users.get(name), "Invalid User")
                .and_then(x -> x.checkPasswd(passwd)
                        ? Result.Ok(x)
                        : Result.Err("Invalid Passwd"));
    }

    List<Music> search_tags(String tag) {
        return this.musics.values()
                .stream()
                .filter(x -> x.contains_tag(tag))
                .collect(Collectors.toList());
    }

    //TODO Some nice file managment
    Result<Music, String> upload_music(Music a) {
        if(this.musics.containsValue(a))
            return Result.Err("Music already exists");
        this.musics.put(a.get_id(), a);
        return Result.Ok(a);
    }
}
