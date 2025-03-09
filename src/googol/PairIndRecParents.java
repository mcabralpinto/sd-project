package googol;
import java.util.Map;
import java.util.Set;

class PairIndRecParents{
		Map<String, Set<String>> indRec;
		Map<String, Set<String>> parents;
		
		PairIndRecParents(Map<String, Set<String>> indRec, Map<String, Set<String>> parents){
				this.indRec = indRec;
				this.parents = parents;
		}
}
