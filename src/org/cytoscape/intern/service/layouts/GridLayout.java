package org.cytoscape.intern.service.layouts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.aspects.datamodels.NodesElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.metadata.MetaDataCollection;

public class GridLayout extends AbstractLayout {

	//TODO make these values modifiable from service parameters
	private static final double NODE_VERTICAL_SPACING = 80d;
	private static final double NODE_HORIZONTAL_SPACING = 100d;
	private ArrayList<NodesElement> nodesToLayOut;
	
	public GridLayout(CxReader cxNodeReader, CxWriter cxLayoutWriter) {
		super(cxNodeReader, cxLayoutWriter);
		
	}

	@Override
	public void apply(MetaDataCollection postLayoutMetadata) throws IOException {
		startLayout();
		double currX = 0.0d;
		double currY = 0.0d;
		double initialX = 0.0d;

		// Yes, our size and starting points need to be different
		final int nodeCount = nodesToLayOut.size();
		final int columns = (int) Math.sqrt(nodeCount);

		int count = 0;

		// Set visual property.
		// TODO: We need batch apply method for Visual Property values for
		// performance.
		for (final NodesElement node : nodesToLayOut) {
			CartesianLayoutElement nodeLayoutElement = new CartesianLayoutElement(node.getId(), currX, currY);
			cxLayoutWriter.writeAspectElement(nodeLayoutElement);

			count++;

			if (count == columns) {
				count = 0;
				currX = initialX;
				currY += NODE_VERTICAL_SPACING;
			} else {
				currX += NODE_HORIZONTAL_SPACING;
			}
		}
		finishLayout();

		postLayoutMetadata.setElementCount(CartesianLayoutElement.ASPECT_NAME, (long) nodesToLayOut.size());
		cxLayoutWriter.addPostMetaData(postLayoutMetadata);
	}

	@Override
	protected void parseInput() throws IOException {
		ArrayList<NodesElement> nodes = new ArrayList<NodesElement>();
		while (cxNodeReader.hasNext()) {
			List<AspectElement> aspectElements = cxNodeReader.getNext();
			for (AspectElement element : aspectElements) {
				if (element.getAspectName().equals(NodesElement.ASPECT_NAME)) {
					nodes.add((NodesElement)element);
				}
			}
		}
		nodesToLayOut = nodes;
	}

}
