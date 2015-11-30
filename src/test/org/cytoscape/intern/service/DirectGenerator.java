package test.org.cytoscape.intern.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.cytoscape.intern.service.LayoutService;

public class DirectGenerator implements Generator {

	@Override
	public InputStream generateCartesianStream(InputStream cxNodeInputStream, String algorithm) {
		ByteArrayOutputStream result = (ByteArrayOutputStream)LayoutService.run(cxNodeInputStream, algorithm);
		InputStream cxCartesianInputStream = new ByteArrayInputStream(result.toByteArray());
		
		return cxCartesianInputStream;
	}

}