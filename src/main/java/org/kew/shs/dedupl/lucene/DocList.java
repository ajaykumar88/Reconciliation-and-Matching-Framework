package org.kew.shs.dedupl.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

/*
 * A very simple wrapper around an ArrayList specific to lucene Documents, that knows whether it's sorted or not.
 * It can so far only add and get Documents and return its size
 */
public class DocList  {

	protected boolean sorted = false;
	protected String sortOn;

	public Document getFromDoc() {
		return fromDoc;
	}

	protected List<Document> store = new ArrayList<Document>();
	protected Document fromDoc;

	public DocList(Document fromDoc, String sortOn) {
		super ();
		this.fromDoc = fromDoc;
		this.store.add(fromDoc); // fromDoc itself is always in the cluster
		this.sortOn = sortOn;
	}

	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	public void sort() {
		if (!this.isSorted()) {
			Collections.sort(this.store, Collections.reverseOrder(new Comparator<Document>() {
				public int compare(final Document d1,final Document d2) {
					return Integer.valueOf(d1.get(sortOn)).compareTo(Integer.valueOf(d2.get(sortOn)));
				}
			}));
			this.setSorted(true);
		}
	}

	public void add(Document doc) {
		this.setSorted(false);
		this.store.add(doc);
	}

	public Document get(int index) {
		return this.store.get(index);
	}

	public String get_attributes(String fieldName, String delimiter) {
		String attributes = "";
		for (int i = 0; i<this.store.size(); i++) {
			Document doc = this.store.get(i);
			if (this.store.indexOf(doc) > 0)
				attributes += delimiter;
			attributes += (doc.get(fieldName));
		}
		return attributes;
	}

	public String[] getFieldNames() {
		List<Fieldable> fields = fromDoc.getFields();
		String[] fieldNames = new String[fields.size()];
		for (int i=0; i< fields.size(); i++) {
			fieldNames[i] = fields.get(i).name();
		}
		return fieldNames;
	}

	public int size () {
		return this.store.size();
	}

}
