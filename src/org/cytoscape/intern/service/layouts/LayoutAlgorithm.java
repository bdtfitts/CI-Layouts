package org.cytoscape.intern.service.layouts;

import java.io.IOException;

import org.cxio.metadata.MetaDataCollection;

public interface LayoutAlgorithm {
	public void apply(MetaDataCollection postLayoutMetadata) throws IOException;
}
