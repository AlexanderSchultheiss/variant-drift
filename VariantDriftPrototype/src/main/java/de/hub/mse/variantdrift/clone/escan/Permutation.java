package de.hub.mse.variantdrift.clone.escan;

//http://stackoverflow.com/questions/11343848/


import de.hub.mse.variantdrift.clone.models.GenericNode;

import java.util.*;

// TODO: Check validity of what this is supposed to do.
public class Permutation {
	public static List<List<GenericNode>> permute(Set<GenericNode> nodes) {

		if (nodes.size() == 1) {
			List<GenericNode> arrayList = new ArrayList<>();
			arrayList.add(nodes.iterator().next());
			List<List<GenericNode>> listOfList = new ArrayList<>();
			listOfList.add(arrayList);
			return listOfList;
		}

		Set<GenericNode> setOf = new HashSet<>(nodes);

		List<List<GenericNode>> listOfLists = new ArrayList<>();

		for (GenericNode i : nodes) {
			ArrayList<GenericNode> arrayList = new ArrayList<>();
			arrayList.add(i);

			Set<GenericNode> setOfCopied = new HashSet<>(setOf);
			setOfCopied.remove(i);

			Set<GenericNode> isttt = new HashSet<>(setOfCopied);

			List<List<GenericNode>> permute = permute(isttt);
			for (List<GenericNode> list : permute) {
				list.add(i);
				listOfLists.add(list);
			}
		}

		return listOfLists;
	}

}