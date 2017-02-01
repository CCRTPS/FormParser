
import java.io.Serializable;

/**
 *
 * @author CCRTPS
 */
public class Label implements Serializable {

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the exclusions
     */
    public String[] getExclusions() {
        return exclusions;
    }

    /**
     * @param exclusions the exclusions to set
     */
    public void setExclusions(String[] exclusions) {
        this.exclusions = exclusions;
    }

    //For serialization purposes - don't change!!
    private static final long serialVersionUID = 7526472295622776147L;

    private String label;
    private String description;
    private String[] exclusions;

    public Label() {

    }

    public Label(String label, String description, String[] exclusions) {
        this.label = label;
        this.description = description;
        this.exclusions = exclusions;
    }
}
