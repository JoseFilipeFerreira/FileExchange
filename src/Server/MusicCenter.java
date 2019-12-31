package Server;

import Utils.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MusicCenter {
    private final Map<Long, Music> musics;
    private final Map<String, User> users;
    private NotificationCenter notify;

    MusicCenter(NotificationCenter n) {
        this.musics = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.notify = n;
    }

    Result<User, String> create_user(User u) {
        synchronized(this.users) {
            if(this.users.containsKey(u.get_name()))
                return Result.Err("User already exists");
            this.users.put(u.get_name(), u);
        }
        return Result.Ok(u);
    }

    Result<User, String> check_login(User u) {
        synchronized(this.users) {
            return Result.of_nullable(this.users.get(u.get_name()), "Invalid User")
                    .and_then(x -> x.equals(u)
                            ? Result.Ok(x)
                            : Result.Err("Invalid Passwd"));
        }
    }

    String search_tags(String tag) {
        synchronized(this.musics) {
            StringBuilder res = new StringBuilder().append("[");
            this.musics.values()
                    .stream()
                    .filter(x -> x.contains_tag(tag))
                    .forEach(x -> res.append("'")
                            .append(x.toString())
                            .append("',"));
            return res.append("]")
                    .toString();
        }
    }

    Result<Long, String> upload_music(Music a) {
        synchronized(this.musics) {
            if(this.musics.containsValue(a))
                return Result.Err("Music already exists");
            this.musics.put(a.get_id(), a);
        }
        this.notify.notify(a);
        return Result.Ok(a.get_id());
    }

    Result<Music, String> download(long id) {
        synchronized(this.musics) {
            return Result.of_nullable(this.musics.get(id), "Invalid Music")
                    .also(Music::download);
        }
    }
}
