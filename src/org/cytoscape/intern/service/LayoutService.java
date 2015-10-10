package org.cytoscape.intern.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

import org.cxio.aspects.readers.EdgesFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.aspects.writers.CartesianLayoutFragmentWriter;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectFragmentReader;
import org.cxio.core.interfaces.AspectFragmentWriter;
import org.cytoscape.intern.service.layouts.GridLayout;
import org.cytoscape.intern.service.layouts.LayoutAlgorithm;

public class LayoutService {

	private static void applyLayout(LayoutAlgorithm layout) {
		layout.apply();
	}
	public static void run(InputStream cxInput) {

		NodesFragmentReader nodesFragmentReader = NodesFragmentReader.createInstance();
		EdgesFragmentReader edgesFragmentReader = EdgesFragmentReader.createInstance();
		CartesianLayoutFragmentWriter cartesianFragmentWriter = CartesianLayoutFragmentWriter.createInstance();

		AspectFragmentWriter[] writerArray = {cartesianFragmentWriter};
		List<AspectFragmentWriter> writers = Arrays.asList(writerArray);

		FileOutputStream outputFileStream = null;
		URL url = ClassLoader.getSystemResource("resources/cxOutput.cx");
		try {
			File file = new File(url.toURI());
			try {
				outputFileStream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Something went wrong!");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException("Something went wrong!");
		}

		AspectFragmentReader[] readerArray = {nodesFragmentReader, edgesFragmentReader};
		List<AspectFragmentReader> readers = Arrays.asList(readerArray);
		try {
			CxReader cxReader = CxReader.createInstance(cxInput, new HashSet<AspectFragmentReader>(readers));
			CxWriter cxWriter = CxWriter.createInstance(outputFileStream, true, new HashSet<AspectFragmentWriter>(writers));
			
			applyLayout(new GridLayout(cxReader, cxWriter));

			outputFileStream.flush();
			outputFileStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Something went wrong!");
		}
	}
	public static void main(String[] args) {
		InputStream cxInput = ClassLoader.getSystemResourceAsStream("resources/cxInput.cx");
		System.out.println("Applying gridlayout on network found in cxInput.cx...");
		run(cxInput);
		System.out.println("Grid layout created.");
	}

}
