public class FileView {
    private String fileName;
    private long size;

    public FileView(String filename, long size) {
        this.fileName = filename;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

}
