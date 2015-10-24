package org.cytoscape.intern.service.layouts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.aspects.datamodels.NodesElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectElement;

public abstract class AbstractLayout implements LayoutAlgorithm {

	protected CxReader cxNodeReader;
	protected CxWriter cxLayoutWriter;
	protected ArrayList<NodesElement> nodesToLayOut;

	public AbstractLayout(CxReader cxNodeReader, CxWriter cxLayoutWriter) {
		this.cxNodeReader = cxNodeReader;
		this.cxLayoutWriter = cxLayoutWriter;

		nodesToLayOut = retrieveNodeElements();
	}
	
	protected void startLayout() throws IOException {
		cxLayoutWriter.start();
		cxLayoutWriter.startAspectFragment(CartesianLayoutElement.NAME);
	}

	protected void finishLayout() throws IOException {
		cxLayoutWriter.endAspectFragment();
		cxLayoutWriter.end();
	}

	@Override
	abstract public void apply();

	private ArrayList<NodesElement> retrieveNodeElements() {
		ArrayList<NodesElement> nodes = new ArrayList<NodesElement>();
		try {
			while (cxNodeReader.hasNext()) {
				List<AspectElement> aspectElements = cxNodeReader.getNext();
				for (AspectElement element : aspectElements) {
					if (element.getAspectName().equals(NodesElement.NAME)) {
						nodes.add((NodesElement)element);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("I/O Exception while reading CX file.");
		}
		return nodes;
	}

}
