package s3.thisisbetter.model;

/**
 * Created by Chloe on 4/7/16.
 */
public class SectionItem implements EventListItem {

    private String title;

    public SectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
