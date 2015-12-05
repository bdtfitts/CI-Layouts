package test.org.cytoscape.intern.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.cxio.aspects.datamodels.CartesianLayoutElement;
import org.cxio.aspects.datamodels.NodesElement;
import org.cxio.aspects.readers.CartesianLayoutFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.core.CxReader;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.core.interfaces.AspectFragmentReader;
import org.junit.Test;

public class LayoutServiceTest {

	@SuppressWarnings("resource")
	public void generate(Generator generator) {
		InputStream cxInputStream = ClassLoader.getSystemResourceAsStream("resources/cxInput.cx");
		NodesFragmentReader nodesFragmentReader = NodesFragmentReader.createInstance();
		HashSet<AspectFragmentReader> hashReader = new HashSet<AspectFragmentReader>();
		hashReader.add(nodesFragmentReader);
		try {
			CxReader cxReader = CxReader.createInstance(cxInputStream, hashReader);
			
			ArrayList<NodesElement> nodes = new ArrayList<NodesElement>();
			while (cxReader.hasNext()) {
				List<AspectElement> aspectElements = cxReader.getNext();
				for (AspectElement element : aspectElements) {
					if (element.getAspectName().equals(NodesElement.ASPECT_NAME)) {
						nodes.add((NodesElement)element);
					}
				}
			}
			cxInputStream.close();

			StringBuilder cxInput = new StringBuilder();
			if (generator instanceof DirectGenerator) {
				cxInputStream = ClassLoader.getSystemResourceAsStream("resources/cxInput.cx");
				BufferedReader cxInputReader = new BufferedReader(new InputStreamReader(cxInputStream));
				String readString = null;
				while ((readString = cxInputReader.readLine()) != null) {
					cxInput.append(readString);
				}
			} else if (generator instanceof RestGenerator) {
				cxInputStream = ClassLoader.getSystemResourceAsStream("resources/cxRestInput.cx");
				BufferedReader cxInputReader = new BufferedReader(new InputStreamReader(cxInputStream));
				String readString = null;
				while ((readString = cxInputReader.readLine()) != null) {
					cxInput.append(readString);
				}
			}
			System.out.println(cxInput.toString());
			InputStream cartesianResult= generator.generateCartesianStream(cxInput.toString(), "grid");
			
			cxInputStream.close();

			CartesianLayoutFragmentReader cartesianFragmentReader = CartesianLayoutFragmentReader.createInstance();
			HashSet<AspectFragmentReader> cartReader = new HashSet<AspectFragmentReader>();
			cartReader.add(cartesianFragmentReader);

			System.out.println("Reading created layout cx file");			
			CxReader layoutReader = CxReader.createInstance(cartesianResult, cartReader);
			ArrayList<CartesianLayoutElement> layout = new ArrayList<CartesianLayoutElement>();
			while (layoutReader.hasNext()) {
				List<AspectElement> cartElements = layoutReader.getNext();
				for (AspectElement element : cartElements) {
					if (element.getAspectName().equals(CartesianLayoutElement.ASPECT_NAME)) {
						layout.add((CartesianLayoutElement)element);
					}
				}
			}
			assertTrue("Number of nodes does not equal number of layout elements", nodes.size() == layout.size());
		} catch (IOException e) {
			fail("Error: " + e.getMessage());
		}
	}
	@Test
	public void testDirectGenSameNumberOfElements() {
		generate(new DirectGenerator());
	}
	@Test
	public void testRestGenSameNumberOfElements() {
		generate(new RestGenerator());
	}

}
