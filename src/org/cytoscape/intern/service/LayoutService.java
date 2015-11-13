package org.cytoscape.intern.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

import org.cxio.aspects.readers.CyVisualPropertiesFragmentReader;
import org.cxio.aspects.readers.EdgesFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.aspects.writers.CartesianLayoutFragmentWriter;
import org.cxio.core.CxReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectFragmentReader;
import org.cxio.core.interfaces.AspectFragmentWriter;
import org.cytoscape.intern.service.layouts.GridLayout;
import org.cytoscape.intern.service.layouts.LayoutAlgorithm;
import org.cytoscape.intern.service.layouts.StackedNodeLayout;

public class LayoutService {
	
	private static final String GRID_LAYOUT = "grid";
	private static final String STACKED_LAYOUT = "stacked";

	private static void applyLayout(LayoutAlgorithm layout) throws IOException {
		layout.apply();
	}
	public static void run(InputStream cxInput, String algorithm) {

		NodesFragmentReader nodesFragmentReader = NodesFragmentReader.createInstance();
		EdgesFragmentReader edgesFragmentReader = EdgesFragmentReader.createInstance();
		CartesianLayoutFragmentWriter cartesianFragmentWriter = CartesianLayoutFragmentWriter.createInstance();

		AspectFragmentWriter[] writerArray = {cartesianFragmentWriter};
		List<AspectFragmentWriter> writers = Arrays.asList(writerArray);

		FileOutputStream outputFileStream = null;
		//Get the resources directory
		URL url = ClassLoader.getSystemResource("resources/");
		Path filePath = Paths.get(url.getPath(), "cxOutput.cx");
		File file = filePath.toFile();
		try {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new RuntimeException("Something went wrong with creating output file");
				}
			}
			outputFileStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find the file to write to");
		}

		List<AspectFragmentReader> readers = new ArrayList<AspectFragmentReader>(2);
		readers.add(nodesFragmentReader);
		readers.add(edgesFragmentReader);
		try {
			CxReader cxReader;
			CxWriter cxWriter = CxWriter.createInstance(outputFileStream, true, new HashSet<AspectFragmentWriter>(writers));
			System.out.println("Applying layout...");
			switch (algorithm) {
				case GRID_LAYOUT: {
					System.out.println("Creating cxReader...");
					cxReader = CxReader.createInstance(cxInput, new HashSet<AspectFragmentReader>(readers));
					System.out.println("Created cxReader.");
					cxWriter.start();
					try {
						applyLayout(new GridLayout(cxReader, cxWriter));
						cxWriter.end(true, "Applied GridLayout to network");
					} catch (IOException e) {
						cxWriter.end(false, "Error when writing to file");
					}
					break;
				}
				case STACKED_LAYOUT: {
					CyVisualPropertiesFragmentReader vizPropFragmentReader = CyVisualPropertiesFragmentReader.createInstance();
					readers.add(vizPropFragmentReader);
					cxReader = CxReader.createInstance(cxInput, new HashSet<AspectFragmentReader>(readers));
					cxWriter.start();
					try {
						applyLayout(new StackedNodeLayout(cxReader, cxWriter));
						cxWriter.end(true, "Applied StackedNodeLayout to network");
					} catch (IOException e) {
						cxWriter.end(false, "Error when writing to file");
					}
					break;
				}
				default: {
					cxWriter.start();
					cxWriter.end(true, "Unable to create layout in accordance to given algorithm");
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to create CX Readers and/or Writers");
		} finally {
			try {
				if (outputFileStream != null) {
					outputFileStream.flush();
					outputFileStream.close();
				}
			} catch (IOException e) {}
		}
	}
	public static void main(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: java LayoutService [input file] [layout algorithm]");
		}
		String inputFilePath = args[0];
		String algorithm = args[1];
		InputStream cxInput = ClassLoader.getSystemResourceAsStream(inputFilePath);
		System.out.println(String.format("Applying %s layout on network specified in %s", algorithm, inputFilePath));
		run(cxInput, algorithm);
		System.out.println(String.format("%s layout applied", algorithm));
	}

}
