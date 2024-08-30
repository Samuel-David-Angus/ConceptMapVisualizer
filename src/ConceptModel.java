import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConceptModel {
    private String concept;
    private final List<ConceptModel> directPrerequisites;
    public ConceptModel(String concept) {
        this.concept = concept;
        this.directPrerequisites = new ArrayList<>();
    }
    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getConcept() {
        return concept;
    }

    public List<ConceptModel> getDirectPrerequisites() {
        return directPrerequisites;
    }
    public void addPrerequisite(ConceptModel conceptModel) {
        directPrerequisites.add(conceptModel);
    }
    public void removePrerequisite(ConceptModel conceptModel) {
        directPrerequisites.remove(conceptModel);
    }
    public Set<ConceptModel> findAllPrerequisites() {
        Set<ConceptModel> allPrerequisites = new HashSet<>(directPrerequisites);
        for (ConceptModel prerequisite: directPrerequisites) {
            allPrerequisites.addAll(prerequisite.findAllPrerequisites());
        }

        return allPrerequisites;
    }
    public boolean checkIfValidPrerequisite(ConceptModel candidatePrerequisite) {
        if (candidatePrerequisite == this || directPrerequisites.contains(candidatePrerequisite)) { // checks if candidatePrerequisite is equal to self or direct prerequisite
            return false;
        }
        // 1st condition checks for loops. 2nd checks if candidate prerequisite is a direct or descended prerequisite
        return !candidatePrerequisite.findAllPrerequisites().contains(this) && !findAllPrerequisites().contains(candidatePrerequisite);
    }
}
