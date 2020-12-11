package de.hub.mse.variantdrift.clone.escan;

//http://stackoverflow.com/questions/11343848/

import org.eclipse.emf.ecore.EObject;

import java.util.*;

public class Permutation {

	public static List<List<EObject>> permute(Set<EObject> nodes) {

		if (nodes.size() == 1) {
			List<EObject> arrayList = new ArrayList<EObject>();
			arrayList.add(nodes.iterator().next());
			List<List<EObject>> listOfList = new ArrayList<List<EObject>>();
			listOfList.add(arrayList);
			return listOfList;
		}

		Set<EObject> setOf = new HashSet<EObject>(nodes);

		List<List<EObject>> listOfLists = new ArrayList<List<EObject>>();

		for (EObject i : nodes) {
			ArrayList<EObject> arrayList = new ArrayList<EObject>();
			arrayList.add(i);

			Set<EObject> setOfCopied = new HashSet<EObject>();
			setOfCopied.addAll(setOf);
			setOfCopied.remove(i);

			Set<EObject> isttt = new HashSet<EObject>(setOfCopied);

			List<List<EObject>> permute = permute(isttt);
			Iterator<List<EObject>> iterator = permute.iterator();
			while (iterator.hasNext()) {
				List<EObject> list = iterator.next();
				list.add(i);
				listOfLists.add(list);
			}
		}

		return listOfLists;
	}

}