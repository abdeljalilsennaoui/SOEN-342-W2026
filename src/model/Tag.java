package model;

public class Tag {
    private String tagKeyword;

    public Tag(String tagKeyword) {
        this.tagKeyword = tagKeyword;
    }

    public String getTagKeyword() { return tagKeyword; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tagKeyword.equalsIgnoreCase(tag.tagKeyword);
    }

    @Override
    public int hashCode() {
        return tagKeyword.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return tagKeyword;
    }
}
