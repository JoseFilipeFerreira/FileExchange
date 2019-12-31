package Client;

public class Upload {
    private String id;
    private String path;

    Upload(String id, String path) {
        this.id = id;
        this.path = path;
    }

    String getId() {
        return id;
    }

    String getPath() {
        return path;
    }
}
