package orf.demo.model;

public class SpellCheck {
    private String word;
    private String status;
    private String error;

    public SpellCheck() {
    }

    public SpellCheck(String word, String status, String error) {
        this.word = word;
        this.status = status;
        this.error = error;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "SpellCheck{" +
                "word='" + word + '\'' +
                ", status='" + status + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}