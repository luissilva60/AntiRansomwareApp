package org.example.models.FileResponse;

public class FileResponse {
    private final int file_id;
    private final String file_name;
    private final String file_path;
    private final String file_hash;
    private final String file_file;
    private final int file_user_id;

    public FileResponse(int file_id, String file_name, String file_path, String file_hash, String file_file, int file_user_id) {
        this.file_id = file_id;
        this.file_name = file_name;
        this.file_path = file_path;
        this.file_hash = file_hash;
        this.file_file = file_file;
        this.file_user_id = file_user_id;
    }

    public int getFile_id() {
        return file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getFile_hash() {
        return file_hash;
    }

    public String getFile_file() {
        return file_file;
    }

    public int getFile_user_id() {
        return file_user_id;
    }
}

