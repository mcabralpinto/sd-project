package googol;
import java.util.Map;
import java.util.Set;

/**
 * Classe que guarda informação para sincronização comunicada entre os barrels e a gateway
 * @author Joao Nuno Coelho
 * @version 1.0
 */
class PairIndRecParents{
		Map<String, Set<String>> indRec;
		Map<String, Set<String>> parents;
		
		PairIndRecParents(Map<String, Set<String>> indRec, Map<String, Set<String>> parents){
				this.indRec = indRec;
				this.parents = parents;
		}
}
