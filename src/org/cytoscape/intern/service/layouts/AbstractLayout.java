package org.cytoscape.intern.service.layouts;

import java.io.IOException;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;

public abstract class AbstractLayout implements LayoutAlgorithm {

	protected CxReader cxNodeReader;
	protected CxWriter cxLayoutWriter;

	public AbstractLayout(CxReader cxNodeReader, CxWriter cxLayoutWriter) {
		this.cxNodeReader = cxNodeReader;
		this.cxLayoutWriter = cxLayoutWriter;
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

}
