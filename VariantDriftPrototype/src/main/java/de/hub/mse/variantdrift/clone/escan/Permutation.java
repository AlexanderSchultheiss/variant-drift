package de.hub.mse.variantdrift.clone.escan;

//http://stackoverflow.com/questions/11343848/

import org.eclipse.emf.ecore.EObject;

import java.util.*;

// TODO: Check validity of what this is supposed to do.
public class Permutation {

	public static List<List<EObject>> permute(Set<EObject> nodes) {

		if (nodes.size() == 1) {
			List<EObject> arrayList = new ArrayList<>();
			arrayList.add(nodes.iterator().next());
			List<List<EObject>> listOfList = new ArrayList<>();
			listOfList.add(arrayList);
			return listOfList;
		}

		Set<EObject> setOf = new HashSet<>(nodes);

		List<List<EObject>> listOfLists = new ArrayList<>();

		for (EObject i : nodes) {
			ArrayList<EObject> arrayList = new ArrayList<>();
			arrayList.add(i);

			Set<EObject> setOfCopied = new HashSet<>(setOf);
			setOfCopied.remove(i);

			Set<EObject> isttt = new HashSet<>(setOfCopied);

			List<List<EObject>> permute = permute(isttt);
			for (List<EObject> list : permute) {
				list.add(i);
				listOfLists.add(list);
			}
		}

		return listOfLists;
	}

}