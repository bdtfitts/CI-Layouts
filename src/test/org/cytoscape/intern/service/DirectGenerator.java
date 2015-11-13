package test.org.cytoscape.intern.service;

import java.io.InputStream;

import org.cytoscape.intern.service.LayoutService;

public class DirectGenerator implements Generator {

	@Override
	public InputStream generateCartesianStream(InputStream cxNodeInputStream, String algorithm) {
		LayoutService.run(cxNodeInputStream, algorithm);
		InputStream cxCartesianInputStream = ClassLoader.getSystemResourceAsStream("resources/cxOutput.cx");
		
		return cxCartesianInputStream;
	}

}
